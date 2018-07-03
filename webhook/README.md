## webhook tool
Recieve push/pull_request events from git repository and send messages to IRC server.

#### Make war file
    $ mvn clean
    $ mvn package

#### Run
    $ java -jar target/webhook-[VERSION].war 
      --server.port=[HTTP Server Port(default "9999")] 
      --irc.server=[IRC Server Host] 
      --irc.port=[IRC Server Port(default "6667")] 
      --irc.name=[IRC Login/NickName(default "webhook")] 
      --irc.channel=[IRC Channel(default #talk")] 
      --irc.encoding=[IRC Message ENCODING(default "ISO-2022-JP")]
   _ ※ "--irc.server" option is required._


#### Setup git repository
  * Case of Gitbucket


    [repository page]->[Settings]->[Service Hooks]->[Add webhook]
    - Payload URL: http://[Server Host]:[server.port]/webhook/api/v1  
    - Content type: application/json
    - Security Token: (blank)
    - Which events would you like to trigger this webhook?: Pull request, Push
    