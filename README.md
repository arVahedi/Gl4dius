# Gl4dius 
#### (Powered by Gl4di4tor)
A very powerful tool for All kinds of MITM attacks.

![Alt Gl4dius](./icon.png)

## Features : 
- deface attack on the whole LAN network
- Phishing attack
- Sniffing attack
- SSL downgrade attack (**SSL-Stripping**)
- Bypass **HSTS** protection
- Cover your track from discovery methods automatically. (**Be Careful**: No guarantee that you will be 
invisible completely. A hacker who understands MITM attacks deeply, can hunt you yet. But it will be challenging for normal 
people or script kiddies to track you.)

## Dependencies : 

- iptables
- jre == 21

## Development :

1. run `mvn clean package` to build the project.
2. run [dev-shell.sh](./misc/installation/dev-env/dev-shell.sh) to start the program into a container.
```shell
./misc/installation/dev-env/dev-shell.sh
```

## Contact me :

To report bugs and suggestion feature, add an issue.

## License

[GNU GENERAL PUBLIC LICENSE Version 3](https://www.gnu.org/licenses/gpl.html)
