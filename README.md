# Minegit

A minecraft plugin allowing to synchronize server using git.


### Setup 

1. Put the plugin into the plugins/ folder
2. Start your server
3. Add your github repository information and your credentials
4. Done ! 


### Usage

#### Commands

- /git reload -> reload the configuration
- /git pull -> pull changes from the remote

#### Automatic pulling

The server will try to pull every 30 seconds from the remote, if the remote has changed.

#### About conflict

About conflicts, there won't be any conflicts, since It's overriding modifications on the server in order to prevent any conflict.
If you want a more manageable plugin go check out Minecicd ! Which inspired me for this work ! 



