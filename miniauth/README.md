# Mini-auth - Here be users

## Wait, what?

So, if we want to have users and passwords, don't you have something for that already?
Say LDAP or AD? So go look and tell me if you find one doing just users, passwords, and groups,
without being (or pretending to be) a full-blown IAM solution with OAUTH or SAML support.

* I don't want to tie myself to AWS, Azure, or such.
* I don't want to force people to log in with their Facebook or Google accounts.
* I only want users and the groups they belong to aka roles they have.

## Approach

* We do CQRS:
    * Only one service to process create/update/delete.
    * That service expects an authenticated caller, so a JWT Token must be present.
    * The claims must include an "auth-admin" role.
    * We use *nix like passwd and groups files.
    * Those files must have been provided in a Docker secret, we must have write-access.
    * When processed and stored in the file, a shared in-memory cache is updated to reflect it.
    * A different service can process the reads.
 