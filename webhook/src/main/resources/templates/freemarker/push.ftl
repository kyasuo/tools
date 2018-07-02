>> Hi, all. Notice the below event.
========================================
 Event: push
 DateTime: ${.now}
 Sender: ${sender}
 Repository: ${name} (${url})
 Ref: ${ref}
 Commits:
  <#list commits as commit>
    - ${commit}
  </#list>
========================================
