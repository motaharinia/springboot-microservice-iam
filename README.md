## Project:

### Project Descriptions:
please see application.properties files in resources folder and select an active profile "dev" or "tst" or "prod" to run project. you can check test methods too.

steps:
- run a mysql docker image [https://gist.github.com/motaharinia][https://gist.github.com/motaharinia]
- run "/externalconfig/sql/DDL.sql" to create project database
- run "/src/test/java/com/motaharinia/client/project/member/presentation/MemberControllerIntegrationTest" to check sample module tests

generate public and private key steps:
- for the first time, download openssl and run these command in its parent folder (it is downloaded before in /externalconfig/generatekey)
- generate keystore:  
  ```keytool -genkeypair -alias authentication-server -keyalg RSA -keypass Motaharinia123456 -keystore auth-server.jks -storepass Motaharinia123456 -validity 180 -keysize 2048 -dname "CN=Motaharinia,OU=DevTeam,O=Company,L=HM,C=IR"```
- migrate to pkcs12:  
  ```keytool -importkeystore -srckeystore auth-server.jks -destkeystore auth-server.jks -deststoretype pkcs12```
- check file and public key:  
  ```keytool -list -rfc --keystore auth-server.jks | "openssl-0.9.8k_X64\bin\openssl.exe" x509 -inform pem -pubkey```
- copy auth-server.jks file in resource/static/security and build the project

### IntellliJ IDEA Configurations:
- IntelijIDEA: Help -> Edit Custom Vm Options -> add these two line:
    - -Dfile.encoding=UTF-8
    - -Dconsole.encoding=UTF-8
- IntelijIDEA: File -> Settings -> Editor -> File Encodings-> Project Encoding: form "System default" to UTF-8. Maybe it affected somehow.
- IntelijIDEA: File -> Settings -> Editor -> File Encodings-> Default Encoding for properties files:  UTF-8.
- IntelijIDEA: File -> Settings -> Editor -> General -> Code Completion -> check "show the documentation popup in 500 ms"
- IntelijIDEA: File -> Settings -> Editor -> General -> Auto Import -> check "Optimize imports on the fly (for current project)"
- IntelijIDEA: File -> Settings -> Editor -> Color Scheme -> Color Scheme Font -> Scheme: Default -> uncheck "Show only monospaced fonts" and set font to "Tahoma"
- IntelijIDEA: Run -> Edit Configuration -> Spring Boot -> XXXApplication -> Configuration -> Environment -> VM Options: -Dspring.profiles.active=dev
- IntelijIDEA: Run -> Edit Configuration -> Spring Boot -> XXXApplication -> Code Coverage -> Fix the package in include box

<hr/>


[https://gist.github.com/motaharinia]: https://gist.github.com/motaharinia