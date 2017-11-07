/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.example.spring.boot.rest.jpa;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.ThreadPoolBuilder;
import org.apache.camel.component.bean.BeanConstants;
import org.apache.camel.component.http.HttpClientConfigurer;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {
	private static final Logger LOG = LoggerFactory.getLogger(Application.class);
	private ThreadPoolBeanMBean threadPoolBean;
	private MBeanServer server;
	private ExecutorService myPool;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	ServletRegistrationBean servletRegistrationBean() {
		ServletRegistrationBean servlet = new ServletRegistrationBean(new CamelHttpTransportServlet(),
				"/camel-rest-jpa/*");
		servlet.setName("CamelServlet");
		return servlet;
	}

	@Bean
	CamelContextConfiguration contextConfiguration() {
		return new CamelContextConfiguration() {
			@Override
			public void beforeApplicationStart(CamelContext context) {
				/*
				HttpConnectionManagerParams myParams = new HttpConnectionManagerParams();
				myParams.setDefaultMaxConnectionsPerHost(1);
				//myParams.setMaxConnectionsPerHost(1);
				MultiThreadedHttpConnectionManager myConnectionManager = new MultiThreadedHttpConnectionManager();
				myConnectionManager.setParams(myParams);
				// should it be http4?
				final HttpComponent http = context.getComponent("http", HttpComponent.class);
				
				http.setHttpConnectionManager(myConnectionManager);
				*/
				
			}

			@Override
			public void afterApplicationStart(CamelContext camelContext) {
				// noop
			}
		};
	}

	@Component
	class RestApi extends RouteBuilder {

		public RestApi() throws Exception {

			CamelContext context = this.getContext();

			ThreadPoolBuilder builder = new ThreadPoolBuilder(context);

			// use thread pool builder to create a custom thread pool
			myPool = (ThreadPoolExecutor) builder.poolSize(5).maxPoolSize(25).maxQueueSize(50).build("MyPool");
			LOG.info(myPool.getClass().toString() + " ExecutorService created");
			// RejectableThreadPoolExecutor

			threadPoolBean = new ThreadPoolBean((ThreadPoolExecutor) myPool);
			server = ManagementFactory.getPlatformMBeanServer();
			server.registerMBean(threadPoolBean, new ObjectName("jmxExample", "name", "threadPoolBean"));

		}

		@Override
		public void configure() throws InterruptedException {
			restConfiguration().contextPath("/camel-rest-jpa").apiContextPath("/api-doc")
					.apiProperty("api.title", "Camel REST API").apiProperty("api.version", "1.0")
					.apiProperty("cors", "true").apiContextRouteId("doc-api").bindingMode(RestBindingMode.json);
			/*
			 * rest("/books").description("Books REST service")
			 * .get("/").description("The list of all the books")
			 * .route().routeId("books-api") .bean(Database.class, "findBooks")
			 * .endRest() .get("order/{id}").description(
			 * "Details of an order by id") .route().routeId("order-api")
			 * .bean(Database.class, "findOrder(${header.id})");
			 */
			rest("/books").description("Books REST service").get("/").description("The list of all the books")
					.to("direct:getBooks").get("order/{id}").description("Details of an order by id")
					.to("direct:getOrder").get("about").to("direct:getAbout");

			from("direct:getBooks").routeId("books-api").bean(Database.class, "findBooks")
					.log("got books on threadpool myPool");

			from("direct:getOrder").routeId("order-api").bean(Database.class, "findOrder(${header.id})")
					.log("got order from db on threadpool myPool");

			from("direct:getAbout").threads().executorService(myPool)

			// .to("netty4-http:https://www.googleapis.com:443/drive/v3/about?bridgeEndpoint=true");
					// .to("http://www.google.com?bridgeEndpoint=true");
					// .to("http://github.com/patfada/pat.git?bridgeEndpoint=true");
					// http://localhost:8089/camel-rest-jpa/books/about
					.log("calling backend").to("http://localhost:8089/camel-rest-jpa/books/about?bridgeEndpoint=true")
					.log("backend returned");

		}
	}

	@Component
	class Backend extends RouteBuilder {

		@Override
		public void configure() {
			// A first route generates some orders and queue them in DB
			from("timer:new-order?delay=1s&period={{example.generateOrderPeriod:2s}}").routeId("generate-order")
					.bean("orderService", "generateOrder").to("jpa:io.fabric8.quickstarts.camel.Order")
					.log("Inserted new order ${body.id}");

			// A second route polls the DB for new orders and processes them
			from("jpa:org.apache.camel.example.spring.boot.rest.jpa.Order" + "?consumer.namedQuery=new-orders"
					+ "&consumer.delay={{example.processOrderPeriod:5s}}"
					+ "&consumeDelete=false").routeId("process-order").log(
							"Processed order #id ${body.id} with ${body.amount} copies of the «${body.book.description}» book");
		}
	}
}