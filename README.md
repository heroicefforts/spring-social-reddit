spring-social-reddit
====================

API Wrapper for [Reddit OAuth2 RESTful web service](http://www.reddit.com/dev/api) 
for [Spring Social](http://projects.spring.io/spring-social/).

~~Note: Although recommended, I have not used Springs RestTemplate for authentication. Reddit requires access_token requests include the clientId and clientSecret in a basic auth header. I don't believe this is possible with RestTemplate.~~ 

Currently Supports authentication via Reddit using OAuth 2. I will update the api to support account account and subreddit settings via REST endpoints in the foreseeable future.


Known Bugs:
- Json parsing errors when using Jackson 1.x. Spring will automatically use the library configured in the main project. If you are using Jackson 1.x there may be errors when parsing The Reddit HTTP response.
 
<hr/>

This fork adds XML configuration support to the Reddit plug-in similar to Twitter and Facebook as seen in [Petri K's demo](https://github.com/pkainulainen/spring-social-examples/tree/master/sign-in/spring-mvc-normal).  It also improves underlying httpclient utilization and fixes issues described in the Note.

1.  Build the jar or download it, install.
2.  Add the [json-simple](http://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple/1.1.1) dependency to your build.
3.  Add the following line to your spring-social.xml spring definition file (substitute [...]):

...<br/>
`xmlns:facebook="http://www.springframework.org/schema/social/facebook"`<br/>
**`+ xmlns:reddit="http://www.springframework.org/schema/social/reddit"`**<br/>
...<br/>
`http://www.springframework.org/schema/social/facebook`<br/> `http://www.springframework.org/schema/social/spring-social-facebook.xsd`<br/>
**`+ http://www.springframework.org/schema/social/reddit`<br/> `http://www.springframework.org/schema/social/spring-social-reddit.xsd"`**<br/>
...<br/>
  `<facebook:config app-id="${facebook.app.id}" app-secret="${facebook.app.pw}"/>`<br/>
  `<twitter:config app-id="${twitter.app.id}" app-secret="${twitter.app.pw}"/>`<br/>
**\+ `<reddit:config app-id="${reddit.app.id}" app-secret="${reddit.app.pw}"
		user-agent="[platform]:[app ID]:[version string] (by /u/[reddit username])" />`**<br/>

Refer to ["The Rules"](https://github.com/reddit/reddit/wiki/API) for more details of Reddit demands.
