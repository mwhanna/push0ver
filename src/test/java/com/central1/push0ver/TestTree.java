/*
Copyright 2016 - Central 1 Credit Union - https://www.central1.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.central1.push0ver;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestTree
{

	private static String testString = "[INFO] Scanning for projects...\n" +
			"[INFO] ------------------------------------------------------------------------\n" +
			"[INFO] Reactor Build Order:\n" +
			"[INFO] \n" +
			"[INFO] TwoSS IdP\n" +
			"[INFO] TwoSS Shibboleth Extension Library\n" +
			"[INFO] Shibboleth\n" +
			"[INFO]                                                                         \n" +
			"[INFO] ------------------------------------------------------------------------\n" +
			"[INFO] Building TwoSS IdP 0.0.0.0.0-SNAPSHOT\n" +
			"[INFO] ------------------------------------------------------------------------\n" +
			"[INFO] \n" +
			"[INFO] --- maven-dependency-plugin:2.1:tree (default-cli) @ twoss-idp ---\n" +
			"[INFO] com.central1.twoss:twoss-idp:pom:0.0.0.0.0-SNAPSHOT\n" +
			"[INFO] +- com.central1.features:twoss-lib:jar:1.0.24:compile\n" +
			"[INFO] |  +- javax.validation:validation-api:jar:1.1.0.Final:compile\n" +
			"[INFO] |  +- com.google.guava:guava:jar:17.0:compile\n" +
			"[INFO] |  +- org.springframework.webflow:spring-webflow:jar:2.4.0.RELEASE:compile\n" +
			"[INFO] |  |  +- commons-logging:commons-logging:jar:1.1.1:compile\n" +
			"[INFO] |  |  +- opensymphony:ognl:jar:2.6.11:compile\n" +
			"[INFO] |  |  +- org.springframework.webflow:spring-binding:jar:2.4.0.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework.webflow:spring-js:jar:2.4.0.RELEASE:compile\n" +
			"[INFO] |  |  |  \\- org.springframework.webflow:spring-js-resources:jar:2.4.0.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework:spring-beans:jar:4.0.2.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework:spring-expression:jar:4.0.2.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework:spring-web:jar:4.0.2.RELEASE:compile\n" +
			"[INFO] |  |  \\- org.springframework:spring-webmvc:jar:4.0.2.RELEASE:compile\n" +
			"[INFO] |  +- org.hibernate:hibernate-core:jar:4.3.8.Final:compile\n" +
			"[INFO] |  |  +- org.jboss.logging:jboss-logging:jar:3.1.3.GA:compile\n" +
			"[INFO] |  |  +- org.jboss.logging:jboss-logging-annotations:jar:1.2.0.Beta1:compile\n" +
			"[INFO] |  |  +- org.jboss.spec.javax.transaction:jboss-transaction-api_1.2_spec:jar:1.0.0.Final:compile\n" +
			"[INFO] |  |  +- dom4j:dom4j:jar:1.6.1:compile\n" +
			"[INFO] |  |  |  \\- xml-apis:xml-apis:jar:1.0.b2:compile\n" +
			"[INFO] |  |  +- org.hibernate.common:hibernate-commons-annotations:jar:4.0.5.Final:compile\n" +
			"[INFO] |  |  +- org.hibernate.javax.persistence:hibernate-jpa-2.1-api:jar:1.0.0.Final:compile\n" +
			"[INFO] |  |  +- org.javassist:javassist:jar:3.18.1-GA:compile\n" +
			"[INFO] |  |  +- antlr:antlr:jar:2.7.7:compile\n" +
			"[INFO] |  |  \\- org.jboss:jandex:jar:1.1.0.Final:compile\n" +
			"[INFO] |  +- org.springframework.data:spring-data-jpa:jar:1.7.2.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework.data:spring-data-commons:jar:1.9.2.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework:spring-orm:jar:4.0.9.RELEASE:compile\n" +
			"[INFO] |  |  |  \\- org.springframework:spring-jdbc:jar:4.0.9.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework:spring-aop:jar:4.0.9.RELEASE:compile\n" +
			"[INFO] |  |  |  \\- aopalliance:aopalliance:jar:1.0:compile\n" +
			"[INFO] |  |  +- org.springframework:spring-tx:jar:4.0.9.RELEASE:compile\n" +
			"[INFO] |  |  +- org.aspectj:aspectjrt:jar:1.8.4:compile\n" +
			"[INFO] |  |  \\- org.slf4j:jcl-over-slf4j:jar:1.7.10:runtime\n" +
			"[INFO] |  +- com.central1.cap.java6:synthetic-key-generator:jar:1.0.1:compile\n" +
			"[INFO] |  |  \\- joda-time:joda-time:jar:2.7:compile\n" +
			"[INFO] |  +- commons-codec:commons-codec:jar:1.10:compile\n" +
			"[INFO] |  \\- com.central1.buildtools:beautifulbeanbuilder:jar:1.33:compile\n" +
			"[INFO] |     +- com.google.code.findbugs:jsr305:jar:3.0.0:compile\n" +
			"[INFO] |     \\- org.hibernate:hibernate-validator:jar:4.2.0.Final:compile\n" +
			"[INFO] +- com.fasterxml.jackson.core:jackson-core:jar:2.5.0:compile\n" +
			"[INFO] +- com.fasterxml.jackson.core:jackson-databind:jar:2.5.0:compile\n" +
			"[INFO] +- com.fasterxml.jackson.core:jackson-annotations:jar:2.5.0:compile\n" +
			"[INFO] +- com.central1:c1-shared:jar:1.0.44:compile\n" +
			"[INFO] |  +- commons-validator:commons-validator:jar:1.4.1:compile\n" +
			"[INFO] |  |  +- commons-beanutils:commons-beanutils:jar:1.8.3:compile\n" +
			"[INFO] |  |  +- commons-digester:commons-digester:jar:1.8.1:compile\n" +
			"[INFO] |  |  \\- commons-collections:commons-collections:jar:3.2.1:compile\n" +
			"[INFO] |  +- org.springframework:spring-context:jar:3.1.0.RELEASE:compile\n" +
			"[INFO] |  |  \\- org.springframework:spring-asm:jar:3.1.0.RELEASE:compile\n" +
			"[INFO] |  +- org.twitter4j:twitter4j-core:jar:3.0.3:compile\n" +
			"[INFO] |  +- commons-lang:commons-lang:jar:2.6:compile\n" +
			"[INFO] |  +- commons-io:commons-io:jar:2.1:compile\n" +
			"[INFO] |  +- org.springframework:spring-core:jar:3.1.2.RELEASE:compile\n" +
			"[INFO] |  \\- org.bouncycastle:bcprov-jdk16:jar:1.46:compile\n" +
			"[INFO] +- org.apache.logging.log4j:log4j-api:jar:2.3:compile\n" +
			"[INFO] +- org.apache.logging.log4j:log4j-core:jar:2.3:compile\n" +
			"[INFO] +- org.slf4j:log4j-over-slf4j:jar:1.7.12:compile\n" +
			"[INFO] |  \\- org.slf4j:slf4j-api:jar:1.7.12:compile\n" +
			"[INFO] +- org.apache.commons:commons-lang3:jar:3.3.2:compile\n" +
			"[INFO] +- net.shibboleth.idp:idp-authn-api:jar:3.1.2:provided\n" +
			"[INFO] |  +- net.shibboleth.idp:idp-profile-api:jar:3.1.2:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-storage-api:jar:3.1.1:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-profile-api:jar:3.1.1:provided\n" +
			"[INFO] |  +- org.ldaptive:ldaptive:jar:1.0.6:provided\n" +
			"[INFO] |  +- javax.json:javax.json-api:jar:1.0:provided\n" +
			"[INFO] |  +- org.glassfish:javax.json:jar:1.0.4:provided\n" +
			"[INFO] |  +- org.codehaus.janino:janino:jar:2.7.8:provided\n" +
			"[INFO] |  |  \\- org.codehaus.janino:commons-compiler:jar:2.7.8:provided\n" +
			"[INFO] |  +- net.shibboleth.utilities:java-support:jar:7.1.1:provided\n" +
			"[INFO] |  +- javax.mail:mail:jar:1.4.7:provided\n" +
			"[INFO] |  |  \\- javax.activation:activation:jar:1.1:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-messaging-api:jar:3.1.1:provided\n" +
			"[INFO] |  \\- org.springframework:spring-context-support:jar:4.1.5.RELEASE:provided\n" +
			"[INFO] +- net.shibboleth.idp:idp-authn-impl:jar:3.1.2:provided\n" +
			"[INFO] |  +- net.shibboleth.idp:idp-attribute-api:jar:3.1.2:provided\n" +
			"[INFO] |  +- net.shibboleth.idp:idp-session-api:jar:3.1.2:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-core:jar:3.1.1:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-soap-api:jar:3.1.1:provided\n" +
			"[INFO] |  |  \\- org.apache.httpcomponents:httpclient:jar:4.3.6:provided\n" +
			"[INFO] |  |     \\- org.apache.httpcomponents:httpcore:jar:4.3.3:provided\n" +
			"[INFO] |  +- net.shibboleth.ext:spring-extensions:jar:5.1.1:provided\n" +
			"[INFO] |  |  \\- org.apache.httpcomponents:httpclient-cache:jar:4.3.6:provided\n" +
			"[INFO] |  \\- org.apache.velocity:velocity:jar:1.7:provided\n" +
			"[INFO] +- net.shibboleth.idp:idp-saml-impl:jar:3.1.2:provided\n" +
			"[INFO] |  +- net.shibboleth.idp:idp-saml-api:jar:3.1.2:provided\n" +
			"[INFO] |  |  \\- net.shibboleth.idp:idp-attribute-resolver-api:jar:3.1.2:provided\n" +
			"[INFO] |  |     \\- net.shibboleth.idp:idp-core:jar:3.1.2:provided\n" +
			"[INFO] |  |        +- com.beust:jcommander:jar:1.47:provided\n" +
			"[INFO] |  |        +- ch.qos.logback:logback-classic:jar:1.1.2:provided\n" +
			"[INFO] |  |        \\- ch.qos.logback:logback-core:jar:1.1.2:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-saml-api:jar:3.1.1:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-xmlsec-api:jar:3.1.1:provided\n" +
			"[INFO] |  \\- org.opensaml:opensaml-security-api:jar:3.1.1:provided\n" +
			"[INFO] |     +- org.apache.santuario:xmlsec:jar:2.0.3:provided\n" +
			"[INFO] |     |  \\- org.codehaus.woodstox:woodstox-core-asl:jar:4.4.1:provided\n" +
			"[INFO] |     |     +- javax.xml.stream:stax-api:jar:1.0-2:provided\n" +
			"[INFO] |     |     \\- org.codehaus.woodstox:stax2-api:jar:3.1.4:provided\n" +
			"[INFO] |     +- org.cryptacular:cryptacular:jar:1.0:provided\n" +
			"[INFO] |     \\- org.bouncycastle:bcprov-jdk15on:jar:1.51:provided\n" +
			"[INFO] +- javax.servlet:javax.servlet-api:jar:3.1.0:provided\n" +
			"[INFO] \\- javax.servlet:jsp-api:jar:2.0:provided\n" +
			"[INFO]    \\- javax.servlet:servlet-api:jar:2.4:provided\n" +
			"[INFO]                                                                         \n" +
			"[INFO] ------------------------------------------------------------------------\n" +
			"[INFO] Building TwoSS Shibboleth Extension Library 0.0.0.0.0-SNAPSHOT\n" +
			"[INFO] ------------------------------------------------------------------------\n" +
			"[INFO] \n" +
			"[INFO] --- maven-dependency-plugin:2.1:tree (default-cli) @ twoss-ext-lib ---\n" +
			"[INFO] com.central1.twoss:twoss-ext-lib:jar:0.0.0.0.0-SNAPSHOT\n" +
			"[INFO] +- junit:junit:jar:4.12:compile\n" +
			"[INFO] |  \\- org.hamcrest:hamcrest-core:jar:1.3:compile\n" +
			"[INFO] +- com.central1.features:twoss-lib:jar:1.0.24:compile\n" +
			"[INFO] |  +- javax.validation:validation-api:jar:1.1.0.Final:compile\n" +
			"[INFO] |  +- com.google.guava:guava:jar:17.0:compile\n" +
			"[INFO] |  +- org.springframework.webflow:spring-webflow:jar:2.4.0.RELEASE:compile\n" +
			"[INFO] |  |  +- commons-logging:commons-logging:jar:1.1.1:compile\n" +
			"[INFO] |  |  +- opensymphony:ognl:jar:2.6.11:compile\n" +
			"[INFO] |  |  +- org.springframework.webflow:spring-binding:jar:2.4.0.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework.webflow:spring-js:jar:2.4.0.RELEASE:compile\n" +
			"[INFO] |  |  |  \\- org.springframework.webflow:spring-js-resources:jar:2.4.0.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework:spring-beans:jar:4.0.2.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework:spring-expression:jar:4.0.2.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework:spring-web:jar:4.0.2.RELEASE:compile\n" +
			"[INFO] |  |  \\- org.springframework:spring-webmvc:jar:4.0.2.RELEASE:compile\n" +
			"[INFO] |  +- org.hibernate:hibernate-core:jar:4.3.8.Final:compile\n" +
			"[INFO] |  |  +- org.jboss.logging:jboss-logging:jar:3.1.3.GA:compile\n" +
			"[INFO] |  |  +- org.jboss.logging:jboss-logging-annotations:jar:1.2.0.Beta1:compile\n" +
			"[INFO] |  |  +- org.jboss.spec.javax.transaction:jboss-transaction-api_1.2_spec:jar:1.0.0.Final:compile\n" +
			"[INFO] |  |  +- dom4j:dom4j:jar:1.6.1:compile\n" +
			"[INFO] |  |  |  \\- xml-apis:xml-apis:jar:1.0.b2:compile\n" +
			"[INFO] |  |  +- org.hibernate.common:hibernate-commons-annotations:jar:4.0.5.Final:compile\n" +
			"[INFO] |  |  +- org.hibernate.javax.persistence:hibernate-jpa-2.1-api:jar:1.0.0.Final:compile\n" +
			"[INFO] |  |  +- org.javassist:javassist:jar:3.18.1-GA:compile\n" +
			"[INFO] |  |  +- antlr:antlr:jar:2.7.7:compile\n" +
			"[INFO] |  |  \\- org.jboss:jandex:jar:1.1.0.Final:compile\n" +
			"[INFO] |  +- org.springframework.data:spring-data-jpa:jar:1.7.2.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework.data:spring-data-commons:jar:1.9.2.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework:spring-orm:jar:4.0.9.RELEASE:compile\n" +
			"[INFO] |  |  |  \\- org.springframework:spring-jdbc:jar:4.0.9.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework:spring-aop:jar:4.0.9.RELEASE:compile\n" +
			"[INFO] |  |  |  \\- aopalliance:aopalliance:jar:1.0:compile\n" +
			"[INFO] |  |  +- org.springframework:spring-tx:jar:4.0.9.RELEASE:compile\n" +
			"[INFO] |  |  +- org.aspectj:aspectjrt:jar:1.8.4:compile\n" +
			"[INFO] |  |  \\- org.slf4j:jcl-over-slf4j:jar:1.7.10:runtime\n" +
			"[INFO] |  +- com.central1.cap.java6:synthetic-key-generator:jar:1.0.1:compile\n" +
			"[INFO] |  |  \\- joda-time:joda-time:jar:2.7:compile\n" +
			"[INFO] |  +- commons-codec:commons-codec:jar:1.10:compile\n" +
			"[INFO] |  \\- com.central1.buildtools:beautifulbeanbuilder:jar:1.33:compile\n" +
			"[INFO] |     +- com.google.code.findbugs:jsr305:jar:3.0.0:compile\n" +
			"[INFO] |     \\- org.hibernate:hibernate-validator:jar:4.2.0.Final:compile\n" +
			"[INFO] +- com.fasterxml.jackson.core:jackson-core:jar:2.5.0:compile\n" +
			"[INFO] +- com.fasterxml.jackson.core:jackson-databind:jar:2.5.0:compile\n" +
			"[INFO] +- com.fasterxml.jackson.core:jackson-annotations:jar:2.5.0:compile\n" +
			"[INFO] +- com.central1:c1-shared:jar:1.0.44:compile\n" +
			"[INFO] |  +- commons-validator:commons-validator:jar:1.4.1:compile\n" +
			"[INFO] |  |  +- commons-beanutils:commons-beanutils:jar:1.8.3:compile\n" +
			"[INFO] |  |  +- commons-digester:commons-digester:jar:1.8.1:compile\n" +
			"[INFO] |  |  \\- commons-collections:commons-collections:jar:3.2.1:compile\n" +
			"[INFO] |  +- org.springframework:spring-context:jar:3.1.0.RELEASE:compile\n" +
			"[INFO] |  |  \\- org.springframework:spring-asm:jar:3.1.0.RELEASE:compile\n" +
			"[INFO] |  +- org.twitter4j:twitter4j-core:jar:3.0.3:compile\n" +
			"[INFO] |  +- commons-lang:commons-lang:jar:2.6:compile\n" +
			"[INFO] |  +- commons-io:commons-io:jar:2.1:compile\n" +
			"[INFO] |  +- org.springframework:spring-core:jar:3.1.2.RELEASE:compile\n" +
			"[INFO] |  \\- org.bouncycastle:bcprov-jdk16:jar:1.46:compile\n" +
			"[INFO] +- org.apache.logging.log4j:log4j-api:jar:2.3:compile\n" +
			"[INFO] +- org.apache.logging.log4j:log4j-core:jar:2.3:compile\n" +
			"[INFO] +- org.slf4j:log4j-over-slf4j:jar:1.7.12:compile\n" +
			"[INFO] |  \\- org.slf4j:slf4j-api:jar:1.7.12:compile\n" +
			"[INFO] +- org.apache.commons:commons-lang3:jar:3.3.2:compile\n" +
			"[INFO] +- net.shibboleth.idp:idp-authn-api:jar:3.1.2:provided\n" +
			"[INFO] |  +- net.shibboleth.idp:idp-profile-api:jar:3.1.2:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-storage-api:jar:3.1.1:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-profile-api:jar:3.1.1:provided\n" +
			"[INFO] |  +- org.ldaptive:ldaptive:jar:1.0.6:provided\n" +
			"[INFO] |  +- javax.json:javax.json-api:jar:1.0:provided\n" +
			"[INFO] |  +- org.glassfish:javax.json:jar:1.0.4:provided\n" +
			"[INFO] |  +- org.codehaus.janino:janino:jar:2.7.8:provided\n" +
			"[INFO] |  |  \\- org.codehaus.janino:commons-compiler:jar:2.7.8:provided\n" +
			"[INFO] |  +- net.shibboleth.utilities:java-support:jar:7.1.1:provided\n" +
			"[INFO] |  +- javax.mail:mail:jar:1.4.7:provided\n" +
			"[INFO] |  |  \\- javax.activation:activation:jar:1.1:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-messaging-api:jar:3.1.1:provided\n" +
			"[INFO] |  \\- org.springframework:spring-context-support:jar:4.1.5.RELEASE:provided\n" +
			"[INFO] +- net.shibboleth.idp:idp-authn-impl:jar:3.1.2:provided\n" +
			"[INFO] |  +- net.shibboleth.idp:idp-attribute-api:jar:3.1.2:provided\n" +
			"[INFO] |  +- net.shibboleth.idp:idp-session-api:jar:3.1.2:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-core:jar:3.1.1:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-soap-api:jar:3.1.1:provided\n" +
			"[INFO] |  |  \\- org.apache.httpcomponents:httpclient:jar:4.3.6:provided\n" +
			"[INFO] |  |     \\- org.apache.httpcomponents:httpcore:jar:4.3.3:provided\n" +
			"[INFO] |  +- net.shibboleth.ext:spring-extensions:jar:5.1.1:provided\n" +
			"[INFO] |  |  \\- org.apache.httpcomponents:httpclient-cache:jar:4.3.6:provided\n" +
			"[INFO] |  \\- org.apache.velocity:velocity:jar:1.7:provided\n" +
			"[INFO] +- net.shibboleth.idp:idp-saml-impl:jar:3.1.2:provided\n" +
			"[INFO] |  +- net.shibboleth.idp:idp-saml-api:jar:3.1.2:provided\n" +
			"[INFO] |  |  \\- net.shibboleth.idp:idp-attribute-resolver-api:jar:3.1.2:provided\n" +
			"[INFO] |  |     \\- net.shibboleth.idp:idp-core:jar:3.1.2:provided\n" +
			"[INFO] |  |        +- com.beust:jcommander:jar:1.47:provided\n" +
			"[INFO] |  |        +- ch.qos.logback:logback-classic:jar:1.1.2:provided\n" +
			"[INFO] |  |        \\- ch.qos.logback:logback-core:jar:1.1.2:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-saml-api:jar:3.1.1:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-xmlsec-api:jar:3.1.1:provided\n" +
			"[INFO] |  \\- org.opensaml:opensaml-security-api:jar:3.1.1:provided\n" +
			"[INFO] |     +- org.apache.santuario:xmlsec:jar:2.0.3:provided\n" +
			"[INFO] |     |  \\- org.codehaus.woodstox:woodstox-core-asl:jar:4.4.1:provided\n" +
			"[INFO] |     |     +- javax.xml.stream:stax-api:jar:1.0-2:provided\n" +
			"[INFO] |     |     \\- org.codehaus.woodstox:stax2-api:jar:3.1.4:provided\n" +
			"[INFO] |     +- org.cryptacular:cryptacular:jar:1.0:provided\n" +
			"[INFO] |     \\- org.bouncycastle:bcprov-jdk15on:jar:1.51:provided\n" +
			"[INFO] +- javax.servlet:javax.servlet-api:jar:3.1.0:provided\n" +
			"[INFO] \\- javax.servlet:jsp-api:jar:2.0:provided\n" +
			"[INFO]    \\- javax.servlet:servlet-api:jar:2.4:provided\n" +
			"[INFO]                                                                         \n" +
			"[INFO] ------------------------------------------------------------------------\n" +
			"[INFO] Building Shibboleth 0.0.0.0.0-SNAPSHOT\n" +
			"[INFO] ------------------------------------------------------------------------\n" +
			"[INFO] \n" +
			"[INFO] --- maven-dependency-plugin:2.10:tree (default-cli) @ shibboleth-idp ---\n" +
			"[INFO] com.central1.twoss:shibboleth-idp:pom:0.0.0.0.0-SNAPSHOT\n" +
			"[INFO] +- com.central1.twoss:twoss-ext-lib:jar:0.0.0.0.0-SNAPSHOT:compile\n" +
			"[INFO] |  \\- junit:junit:jar:4.12:compile\n" +
			"[INFO] |     \\- org.hamcrest:hamcrest-core:jar:1.3:compile\n" +
			"[INFO] +- com.central1.features:twoss-lib:jar:1.0.24:compile\n" +
			"[INFO] |  +- javax.validation:validation-api:jar:1.1.0.Final:compile\n" +
			"[INFO] |  +- com.google.guava:guava:jar:17.0:compile\n" +
			"[INFO] |  +- org.springframework.webflow:spring-webflow:jar:2.4.0.RELEASE:compile\n" +
			"[INFO] |  |  +- commons-logging:commons-logging:jar:1.1.1:compile\n" +
			"[INFO] |  |  +- opensymphony:ognl:jar:2.6.11:compile\n" +
			"[INFO] |  |  +- org.springframework.webflow:spring-binding:jar:2.4.0.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework.webflow:spring-js:jar:2.4.0.RELEASE:compile\n" +
			"[INFO] |  |  |  \\- org.springframework.webflow:spring-js-resources:jar:2.4.0.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework:spring-beans:jar:4.0.2.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework:spring-expression:jar:4.0.2.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework:spring-web:jar:4.0.2.RELEASE:compile\n" +
			"[INFO] |  |  \\- org.springframework:spring-webmvc:jar:4.0.2.RELEASE:compile\n" +
			"[INFO] |  +- org.hibernate:hibernate-core:jar:4.3.8.Final:compile\n" +
			"[INFO] |  |  +- org.jboss.logging:jboss-logging:jar:3.1.3.GA:compile\n" +
			"[INFO] |  |  +- org.jboss.logging:jboss-logging-annotations:jar:1.2.0.Beta1:compile\n" +
			"[INFO] |  |  +- org.jboss.spec.javax.transaction:jboss-transaction-api_1.2_spec:jar:1.0.0.Final:compile\n" +
			"[INFO] |  |  +- dom4j:dom4j:jar:1.6.1:compile\n" +
			"[INFO] |  |  |  \\- xml-apis:xml-apis:jar:1.0.b2:compile\n" +
			"[INFO] |  |  +- org.hibernate.common:hibernate-commons-annotations:jar:4.0.5.Final:compile\n" +
			"[INFO] |  |  +- org.hibernate.javax.persistence:hibernate-jpa-2.1-api:jar:1.0.0.Final:compile\n" +
			"[INFO] |  |  +- org.javassist:javassist:jar:3.18.1-GA:compile\n" +
			"[INFO] |  |  +- antlr:antlr:jar:2.7.7:compile\n" +
			"[INFO] |  |  \\- org.jboss:jandex:jar:1.1.0.Final:compile\n" +
			"[INFO] |  +- org.springframework.data:spring-data-jpa:jar:1.7.2.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework.data:spring-data-commons:jar:1.9.2.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework:spring-orm:jar:4.0.9.RELEASE:compile\n" +
			"[INFO] |  |  |  \\- org.springframework:spring-jdbc:jar:4.0.9.RELEASE:compile\n" +
			"[INFO] |  |  +- org.springframework:spring-aop:jar:4.0.9.RELEASE:compile\n" +
			"[INFO] |  |  |  \\- aopalliance:aopalliance:jar:1.0:compile\n" +
			"[INFO] |  |  +- org.springframework:spring-tx:jar:4.0.9.RELEASE:compile\n" +
			"[INFO] |  |  +- org.aspectj:aspectjrt:jar:1.8.4:compile\n" +
			"[INFO] |  |  \\- org.slf4j:jcl-over-slf4j:jar:1.7.10:runtime\n" +
			"[INFO] |  +- com.central1.cap.java6:synthetic-key-generator:jar:1.0.1:compile\n" +
			"[INFO] |  +- commons-codec:commons-codec:jar:1.10:compile\n" +
			"[INFO] |  \\- com.central1.buildtools:beautifulbeanbuilder:jar:1.33:compile\n" +
			"[INFO] |     \\- org.hibernate:hibernate-validator:jar:4.2.0.Final:compile\n" +
			"[INFO] +- com.fasterxml.jackson.core:jackson-core:jar:2.5.0:compile\n" +
			"[INFO] +- com.fasterxml.jackson.core:jackson-databind:jar:2.5.0:compile\n" +
			"[INFO] +- com.fasterxml.jackson.core:jackson-annotations:jar:2.5.0:compile\n" +
			"[INFO] +- com.central1:c1-shared:jar:1.0.44:compile\n" +
			"[INFO] |  +- commons-validator:commons-validator:jar:1.4.1:compile\n" +
			"[INFO] |  |  +- commons-beanutils:commons-beanutils:jar:1.8.3:compile\n" +
			"[INFO] |  |  +- commons-digester:commons-digester:jar:1.8.1:compile\n" +
			"[INFO] |  |  \\- commons-collections:commons-collections:jar:3.2.1:compile\n" +
			"[INFO] |  +- org.springframework:spring-context:jar:3.1.0.RELEASE:compile\n" +
			"[INFO] |  |  \\- org.springframework:spring-asm:jar:3.1.0.RELEASE:compile\n" +
			"[INFO] |  +- org.twitter4j:twitter4j-core:jar:3.0.3:compile\n" +
			"[INFO] |  +- commons-lang:commons-lang:jar:2.6:compile\n" +
			"[INFO] |  +- commons-io:commons-io:jar:2.1:compile\n" +
			"[INFO] |  +- org.springframework:spring-core:jar:3.1.2.RELEASE:compile\n" +
			"[INFO] |  \\- org.bouncycastle:bcprov-jdk16:jar:1.46:compile\n" +
			"[INFO] +- org.apache.logging.log4j:log4j-api:jar:2.3:compile\n" +
			"[INFO] +- org.apache.logging.log4j:log4j-core:jar:2.3:compile\n" +
			"[INFO] +- org.slf4j:log4j-over-slf4j:jar:1.7.12:compile\n" +
			"[INFO] |  \\- org.slf4j:slf4j-api:jar:1.7.12:compile\n" +
			"[INFO] +- org.apache.commons:commons-lang3:jar:3.3.2:compile\n" +
			"[INFO] +- net.shibboleth.idp:idp-authn-api:jar:3.1.2:provided\n" +
			"[INFO] |  +- net.shibboleth.idp:idp-profile-api:jar:3.1.2:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-storage-api:jar:3.1.1:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-profile-api:jar:3.1.1:provided\n" +
			"[INFO] |  +- org.ldaptive:ldaptive:jar:1.0.6:provided\n" +
			"[INFO] |  +- javax.json:javax.json-api:jar:1.0:provided\n" +
			"[INFO] |  +- joda-time:joda-time:jar:2.7:compile\n" +
			"[INFO] |  +- org.glassfish:javax.json:jar:1.0.4:provided\n" +
			"[INFO] |  +- com.google.code.findbugs:jsr305:jar:3.0.0:compile\n" +
			"[INFO] |  +- org.codehaus.janino:janino:jar:2.7.8:provided\n" +
			"[INFO] |  |  \\- org.codehaus.janino:commons-compiler:jar:2.7.8:provided\n" +
			"[INFO] |  +- net.shibboleth.utilities:java-support:jar:7.1.1:provided\n" +
			"[INFO] |  +- javax.mail:mail:jar:1.4.7:provided\n" +
			"[INFO] |  |  \\- javax.activation:activation:jar:1.1:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-messaging-api:jar:3.1.1:provided\n" +
			"[INFO] |  \\- org.springframework:spring-context-support:jar:4.1.5.RELEASE:provided\n" +
			"[INFO] +- net.shibboleth.idp:idp-authn-impl:jar:3.1.2:provided\n" +
			"[INFO] |  +- net.shibboleth.idp:idp-attribute-api:jar:3.1.2:provided\n" +
			"[INFO] |  +- net.shibboleth.idp:idp-session-api:jar:3.1.2:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-core:jar:3.1.1:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-soap-api:jar:3.1.1:provided\n" +
			"[INFO] |  |  \\- org.apache.httpcomponents:httpclient:jar:4.3.6:provided\n" +
			"[INFO] |  |     \\- org.apache.httpcomponents:httpcore:jar:4.3.3:provided\n" +
			"[INFO] |  +- net.shibboleth.ext:spring-extensions:jar:5.1.1:provided\n" +
			"[INFO] |  |  \\- org.apache.httpcomponents:httpclient-cache:jar:4.3.6:provided\n" +
			"[INFO] |  \\- org.apache.velocity:velocity:jar:1.7:provided\n" +
			"[INFO] +- net.shibboleth.idp:idp-saml-impl:jar:3.1.2:provided\n" +
			"[INFO] |  +- net.shibboleth.idp:idp-saml-api:jar:3.1.2:provided\n" +
			"[INFO] |  |  \\- net.shibboleth.idp:idp-attribute-resolver-api:jar:3.1.2:provided\n" +
			"[INFO] |  |     \\- net.shibboleth.idp:idp-core:jar:3.1.2:provided\n" +
			"[INFO] |  |        +- com.beust:jcommander:jar:1.47:provided\n" +
			"[INFO] |  |        +- ch.qos.logback:logback-classic:jar:1.1.2:provided\n" +
			"[INFO] |  |        \\- ch.qos.logback:logback-core:jar:1.1.2:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-saml-api:jar:3.1.1:provided\n" +
			"[INFO] |  +- org.opensaml:opensaml-xmlsec-api:jar:3.1.1:provided\n" +
			"[INFO] |  \\- org.opensaml:opensaml-security-api:jar:3.1.1:provided\n" +
			"[INFO] |     +- org.apache.santuario:xmlsec:jar:2.0.3:provided\n" +
			"[INFO] |     |  \\- org.codehaus.woodstox:woodstox-core-asl:jar:4.4.1:provided\n" +
			"[INFO] |     |     +- javax.xml.stream:stax-api:jar:1.0-2:provided\n" +
			"[INFO] |     |     \\- org.codehaus.woodstox:stax2-api:jar:3.1.4:provided\n" +
			"[INFO] |     +- org.cryptacular:cryptacular:jar:1.0:provided\n" +
			"[INFO] |     \\- org.bouncycastle:bcprov-jdk15on:jar:1.51:provided\n" +
			"[INFO] +- javax.servlet:javax.servlet-api:jar:3.1.0:provided\n" +
			"[INFO] \\- javax.servlet:jsp-api:jar:2.0:provided\n" +
			"[INFO]    \\- javax.servlet:servlet-api:jar:2.4:provided\n" +
			"[INFO] ------------------------------------------------------------------------\n" +
			"[INFO] Reactor Summary:\n" +
			"[INFO] \n" +
			"[INFO] TwoSS IdP ......................................... SUCCESS [1.977s]\n" +
			"[INFO] TwoSS Shibboleth Extension Library ................ SUCCESS [0.186s]\n" +
			"[INFO] Shibboleth ........................................ SUCCESS [0.492s]\n" +
			"[INFO] ------------------------------------------------------------------------\n" +
			"[INFO] BUILD SUCCESS\n" +
			"[INFO] ------------------------------------------------------------------------\n" +
			"[INFO] Total time: 2.948s\n" +
			"[INFO] Finished at: Fri Feb 26 16:09:26 PST 2016\n" +
			"[INFO] Final Memory: 27M/1237M\n" +
			"[INFO] ------------------------------------------------------------------------";

	@Test
	public void testCases()
	{
		List<String> moduleNames = new ArrayList<String>();
		List<String> groupNames = new ArrayList<String>();
		int swi = 0;
		List<String> complete = new ArrayList<String>();
		String[] testStrings = testString.split( "\n" );
		for ( String d : testStrings )
		{
			if ( swi == 1 )
			{
				if ( d.substring( 0, 6 ).equals( "[INFO]" ) && !d.contains( "--" ) )
				{

					complete.add( d.substring( 7 ) );
				}
				swi = 0;
			}
			if ( d.contains( "maven-dependency-plugin" ) && d.contains( ":tree" ) )
			{
				swi = 1;

			}
		}

		for ( String b : complete )
		{
			System.out.println( b );
		}

	}
}
