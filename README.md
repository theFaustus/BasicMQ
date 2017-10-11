# BasicMQ - A XML based protocol
Description of the BasicMQ Protocol.
### List of supported commands:
- **send** - Send a simple message assigned by default to "default" queue or to a named queue.
*Example:*
```
<command>   
    <type>send</type>   
    <queueName>default</queueName>   
    <body>Hello!</body>
</command>
```
- **send_regex** - Multicast a message to all the queues that are matching the RegEx pattern between the queueName tags.
*Example:*
```
<command>   
    <type>send</type>   
    <queueName>G.+</queueName>   
    <body>Hello!</body>
</command>
```
- **receive** - Receive a message by requesting it with a default queue or a named one.
*Example:*
```
<command>   
    <type>receive</type>   
    <queueName>default</queueName>   
    <body></body>
</command>
```
- **acknowledge** - Send a message about receiving the message.
*Example:*
```
<command>   
    <type>acknowledge</type>   
    <queueName></queueName>   
    <body>message_id</body>
</command>
```
- **create_queue** - Create a named queue.
*Example:*
```
<command>   
    <type>create_queue</type>   
    <queueName>Google</queueName>   
    <body></body>
</command>
```
- **delete_queue** - Delete a named queue.
*Example:*
```
<command>   
    <type>delete_queue</type>   
    <queueName>Google</queueName>   
    <body></body>
</command>
```
- **list_queues** - Request a list of the available queues.
*Example:*
```
<command>   
    <type>list_queues</type>   
    <queueName></queueName>   
    <body></body>
</command>
```
- **subscribe** - Subscribe to a queue.
*Example:*
```
<command>   
    <type>subscribe</type>   
    <queueName>Google</queueName>   
    <body></body>
</command>
```
- **subscribe_regex** - Subscribe to a list of queues that are matching the RegEx pattern between the queueName tags.
*Example:*
```
<command>   
    <type>subscribe</type>   
    <queueName>Google</queueName>   
    <body></body>
</command>
```
### List of supported responses:
- **MSG_OK** - Is returned when the client requests a message and this is the server`s response.
*Example:*
```
<response>   
    <optionalMessage>Hello!</optionalMessage>   
    <status>MSG_OK</status>   
    <errorDescription></errorDescription>
</response>
```
- **MSG_PBL** - Is returned when the client is a subscriber and on his queue was published a message or when client is subscribing himself.
*Example:*
```
<response>   
    <optionalMessage>Hello!</optionalMessage>   
    <status>MSG_PBL</status>   
    <errorDescription></errorDescription>
</response>
```
- **MSG_ERROR** - Is returned when the server got an error.
*Example:*
```
<response>   
    <optionalMessage></optionalMessage>   
    <status>MSG_ERROR</status>   
    <errorDescription>stacktrace</errorDescription>
</response>
```
