# auth-server - Authenticate users and return JWT tokens

## Approach

This thing will behave like a minimalist OAuth 2.0 server or OpenID Connect,
in the sense that we respond to HTTP POST requests with (as form data) "grant_type"
equal to "token" and "scope" equal to "fs.rakis.nl". A User ID and Password are
passed using HTTP BASIC Authentication, in an "Authorization" header. The return
value is a JSON record of the user, with a JWT token in a new "Authorization"
header. This last header must be passed with all subsequent calls.

## Wait, what?

So I don't want to go through the woes of installing and integrating a full OAuth
or OpenID Connect stack. Sorry.  
