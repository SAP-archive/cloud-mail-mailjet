# Using Mailjet Email Service from HANA Cloud Platform

This project should be used as a starting point for implementing HANA Cloud Platform (HCP) applications in Java using Mailjet's email service capabilities. It exemplifies the basic usage of Mailjet's APIs from an application deployed on HCP. 

## Preliminaries

In order to use the Mailjet email service, one must first sign up on [mailjet.com](http://mailjet.com). For the newly created account Mailjet provides an API Key and a Secret Key. These credentials can be retrieved [here](https://app.mailjet.com/account/api_keys). To access the Mailjet API from HCP, you need to create two destinations named MAILJETAPI and MAILJETSMTP on application level. For convenience, import the destinations found in this folder: `/src/main/resources/`. Complete the destination authentication for both destinations by adding the Mailjet credentials as user and password.

Mailjet provides a Java library that facilitates sending emails programmatically. This library is not yet available on Maven Central, so you need to download the jar from [github](https://github.com/mailjet/mailjet-apiv3-java). Copy it into the `/repo/com/mailjet/client/LIBRARY_VERSION/` folder and add the following dependency to the pom.xml:

```XML
<dependency>
	<groupId>com.mailjet</groupId>
	<artifactId>client</artifactId>
	<version>LIBRARY_VERSION</version>
</dependency>
```

Please replace `LIBRARY_VERSION` with the current version number, both in the pom and the folder path. This project was tested with version 3.0.0 of the library.

To demonstrate Mailjet's Event and Parse API, please fill in the missing information into the following properties file for the application to work correctly: `/src/main/webapp/WEB-INF/lib/mailjet.properties`.

Use Maven to build the project and then deploy it on HCP.

For a detailed explanation of the code, please follow the [wiki](https://github.com/SAP/cloud-mail-mailjet/wiki). 

## Usage

The application can be used to explore different capabilities of Mailjet: how you can send emails, how to get notifications on email-related events, and how Mailjet can be configured to parse and forward incoming emails for automated processing.

### Use Case 1 - Sending emails
via SMTP / using Mailjet's Web API / using Mailjet's Java library
Once it is started, access the application's URL and you should be able to see a simple form. Enter the **From**, **To**, **Subject** and **Mail Text** fields. To actually send off the email you have 3 buttons: you may send via SMTP or via HTTP, the latter with or without using Mailjet's Java Wrapper. In any case, your email should've been sent successfully.

### Use Case 2 - Email events notifications
Go to the [Event tracking](https://app.mailjet.com/account/triggers) section of your Mailjet account settings. Configure the Endpoint URL to point to `https://<your_hcp_app_url>/mailevent`. From the events list, select "Bounce events". This means that we've configured our Mailjet account to post a notification to the indicated URL (which is one of our app's servlets) whenever a sent email is bounced.

Now, return to the web ui and send an email to "fiagdiaufga97823ryei@ffkdsauioh78h.iysdf" (or to any random unexisting email address). The email will be obviously dropped, but you will receive an email at the address specified in the **mailjet.properties** file, in the EMAIL_EVENT_TO field.

### Use Case 3 - Inbound email parse ###
Through this use case we demonstrate the usage of Sendgrid APIs on incoming emails.
For that, you have to go to follow the [setup steps](http://dev.mailjet.com/guides/#parse-api-inbound-emails) described by Mailjet, the parseroute Url being `https://<your_hcp_app_url>/inbound`.

Once you are done, simply send (using any email client) an email to an address belonging to your domain. Since we parse the subject, try adding one of the following keywords in there: *fw:, ref:, support, error, question, order*. 

You will receive an email at the address specified in the **mailjet.properties** file, in the INBOUND_TO field. The email will contain information about the incoming email, and it will have had it categorized already based on its subject. This way, we've demonstrated how Mailjet's inbound parse webhook can be used to route the emails appropriately (of course, more advanced routing can be put in place, but the present code just showcases the feature).

For more information about the HANA Cloud Platform, please have a look [here](http://hcp.sap.com/). 
For more information about Mailjet, please access this [link](https://mailjet.com/).

## Copyright and license ##

Copyright (c) 2015 SAP SE

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

----------

This program references/bundles the following third party open source or other free download components. 
The third party licensors of these components may provide additional license rights, 
terms and conditions and/or require certain notices as described below. 

jQuery (http://jquery.com/)
Licensed under MIT - https://github.com/jquery/jquery/blob/master/MIT-LICENSE.txt

Twitter Bootstrap (http://twitter.github.com/bootstrap/)
Licensed under Apache License, Version 2.0 - http://www.apache.org/licenses/LICENSE-2.0

json.org (json.org)
Licensed under json.org License (http://www.json.org/license.html)

Mailjet Java API Wrapper (https://github.com/mailjet/mailjet-apiv3-java)
Licensed under MIT License (https://github.com/mailjet/mailjet-apiv3-java/blob/master/LICENCE)
