# About this project

> - I built this project as a way to learn Java Http Server by myself.
> - The libraries in the libraries folder were written from scratch by me to better understand how to design libraries for projects.
> - IDE `VSCode`

# Extensions Requirement

> - `Extension Pack For Java`

# Before run this project

> [!important]
>
> - This project require jdbc driver to connect mysql database [MySQL Community Downloads](https://dev.mysql.com/downloads/connector/j/)
> - Config jdbc path on settings.json in .vscode folder `java.project.referencedLibraries`, example path: `drivers/mysql-connector-j-9.0.0/mysql-connector-j-9.0.0.jar`.
> - Reset configuration parameters in config.json

# Run Project

> - Open terminal in project folder and run bash:
>   <code>java -cp "drivers/mysql-connector-j-9.0.0/mysql-connector-j-9.0.0.jar" src/Server.java</code>
>   OR
> - Ctrl + F5 `In VSCode` to run project (Require extension: `Extension pack for java`, )
