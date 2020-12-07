# finding-key-memory

## Disclaimer

>All the information provided on this tutorial is for educational purposes only. The information on this tutorial should only be used to enhance the security for your computer systems and not for causing malicious or damaging attacks.
>
>You should not misuse this information to gain unauthorized access into computer systems. Also be aware, performing hack attempts on computers that you do not own, without written permission from owners, is illegal.
>
>PT Dymar Jaya Indonesia will not be responsible for any direct or indirect damage caused due to the usage of the information provided on this tutorial.
---
>Semua informasi yang diberikan pada tutorial ini bertujuan untuk edukasi. Informasi pada tutorial ini sebaiknya hanya digunakan untuk meningkatkan keamanan sistem komputer Anda dan bukan untuk menyebabkan serangan jahat atau merusak.
>
>Anda tidak boleh menyalahgunakan informasi ini untuk mendapatkan akses tidak sah ke sistem komputer. Ketahuilah bahwa, melakukan upaya peretasan pada komputer yang bukan milik Anda, tanpa izin tertulis dari pemilik, adalah ilegal.
>
>PT Dymar Jaya Indonesia tidak akan bertanggung jawab atas kerusakan langsung atau tidak langsung yang disebabkan karena penggunaan informasi yang diberikan pada tutorial ini.
<br>

## Latar Belakang
Kriptografi seperti enkripsi ataupun digital signature digunakan di dunia digital dengan tujuan untuk mengamankan aset berharga yang dimiliki. Proses kriptografi, secara umum dapat dilakukan menggunakan software. Namun proses kriptografi pada software memiliki beberapa kelemahan. Salah satunya, key enkripsi yang digunakan untuk proses kriptografi akan berada di memory komputer. Secara teknologi, sangat memungkinkan untuk menemukan nilai key pada memory komputer, tidak tertutup pada jenis Operating System (OS) ataupun bahasa pemrograman tertentu. Apabila key enkripsi ini dapat diketahui atau ditemukan nilainya, tentunya dapat digunakan untuk membuka semua data yang terproteksi key enkripsi tersebut.
<br>

## Deskripsi
Pada tutorial ini akan dibahas secara sederhana bagaimana proses menemukan key enkripsi (untuk algoritma AES dan RSA) pada aplikasi yang sedang terproses di komputer.
<br>

## Enviroment Test
* Operating System: Windows 10, Linux Ubuntu 18
* Language: Java JDK 11, Golang v1.13 
* Additional tool: OpenSSL
<br>

## 1. Menemukan AES Secret Key pada Aplikasi Enkripsi Java yang Running di Linux
Copy folder `finding-key-memory` ke Linux.

Gunakan terminal, buka folder `java-aes`.
```
$ cd finding-key-memory/java-aes
```

Generate AES key terproteksi keystore.
```
keytool -genseckey -alias aeskey -keyalg AES -keysize 256 -storetype pkcs12 -keystore keystore.p12
``` 
Masukkan password sembarang (contoh: 123456). Proses ini akan mengenerate random key AES 256 bit dan menyimpannya ke dalam keystore terproteksi password.

Compile program encryptKS.java dan jalankan aplikasi `encryptKS`:
```
$ javac encryptKS.java 
$ java encryptKS 
Usage: 
java encryptKS <Keystore name> <Alias key> <Keystore Password> <Plaintext>

$ java encryptKS keystore.p12 aeskey 123456 exampleplaintext
load key with alias keyname: aeskey, in keystore: keystore.p12
plaintext	: exampleplaintext
ciphertext	: c98157a652292c541345d933969bd57625f08bf58095bba5b22188c417c25272

Pause the process ... Press any key to resume & exit the application.
```
Aplikasi java encryptKS akan melakukan load AES secret key dari keystore dan melakukan proses enkripsi, kemudian aplikasi di pause untuk mensimulasikan bahwa aplikasi masih tetap running sebagai service. Pada proses ini sebenarnya key terload di memory komputer.

Untuk mencari AES secret key di memory komputer pada proses aplikasi yang sedang running, dapat menggunakan program aes-finder. Untuk menginstalnya buka terminal baru, kemudian menuju folder `aes-finder`.
```
cd finding-key-memory/aes-finder
```

Compile program AES Finder:
```
$ g++ -O3 -march=native -fomit-frame-pointer aes-finder.cpp -o aes-finder
```
>**Note**: Program ini juga dapat digunakan pada Windows dengan menggunakan MinGW
>
Setelah di compile akan menghasilkan file `aes-finder` executable. 

Jalankan command `ps` untuk melihat nama proses atau PID yang sedang running:
```
$ ps -a
2511 pts/0    00:00:01 java
2574 pts/1    00:00:00 ps
```
Akan terlihat proses aplikasi java dan PID nya. Pada contoh ini PID nya adalah 2511.

Jalankan program `aes-finder`. Gunakan kata 'java' sebagai argumennya. Karena proses yang akan kita analisa adalah proses java. Gunakan authorise user untuk eksekusi command ini.
```
$ sudo ./aes-finder java
[sudo] password for user: 
Searching PID 2511 ...
[0xe3053df8] Found AES-256 encryption key: c2cad596df9efdd27f3a78ad3b33f174c7abbbdd4dd18518f3edb334a6828dd6
Processed 311.27 MB, speed = 20.14 MB/s
Done!
```
Terlihat ditemukan nilai key AES 256 bit.

Untuk membuktikan bahwa key yang ditemukan adalah benar, kembali ke terminal pada aplikasi `java-aes` dan jalankan aplikasi decrypt.java

Compile decrypt.java terlebih dahulu kemudian cobalah decrypt ciphertext yang dihasilkan pada aplikasi `encryptKS` dengan key yang ditemukan oleh aplikasi `aes-finder`.
```
$ javac decrypt.java
$ java decrypt 
Usage: 
java decrypt <Clear key in Hex> <Ciphertext in Hex>
$ java decrypt c2cad596df9efdd27f3a78ad3b33f174c7abbbdd4dd18518f3edb334a6828dd6 c98157a652292c541345d933969bd57625f08bf58095bba5b22188c417c25272
clear key	: c2cad596df9efdd27f3a78ad3b33f174c7abbbdd4dd18518f3edb334a6828dd6
ciphertext	: c98157a652292c541345d933969bd57625f08bf58095bba5b22188c417c25272
plaintext	: exampleplaintext
```
Dari hasil decrypt terlihat nilai plaintext adalah benar yaitu `exampleplaintext`. Dengan kata lain nilai clear key AES 256 bit berhasil ditemukan.
<br>

## 2. Menemukan RSA Private Key pada Aplikasi Digital Signature Java yang Running di Windows
Copy folder `finding-key-memory` ke Windows.

Gunakan command prompt, buka folder `java-rsa`.
```
> cd finding-key-memory\java-rsa
```
Gunakan command di bawah ini untuk mengenerate RSA 2048 bit key. Pada contoh program ini masukkan nilai 123456 sebagai passwordnya. Kemudian isi data yang diminta sesuai profile Anda.
```
> keytool -genkey -storetype pkcs12 -keyalg RSA -sigalg SHA256withRSA -keystore keystore.p12 -alias rsakey -keysize 2048
Enter keystore password:
Re-enter new password:
What is your first and last name?
  [Unknown]:  dymar jaya
What is the name of your organizational unit?
  [Unknown]:  sales marketing
What is the name of your organization?
  [Unknown]:  dymar jaya indonesia
What is the name of your City or Locality?
  [Unknown]:  jakarta
What is the name of your State or Province?
  [Unknown]:  dki jakarta
What is the two-letter country code for this unit?
  [Unknown]:  id
Is CN=dymar jaya, OU=sales marketing, O=dymar jaya indonesia, L=jakarta, ST=dki jakarta, C=id correct?
  [no]:  yes

Generating 2,048 bit RSA key pair and self-signed certificate (SHA256withRSA) with a validity of 90 days
        for: CN=dymar jaya, OU=sales marketing, O=dymar jaya indonesia, L=jakarta, ST=dki jakarta, C=id
```
Proses di atas akan menghasilkan random RSA 2048 bit key yang tersimpan di keystore terproteksi password.

Compile dan jalankan program SignatureRSA.java
```
> javac signatureRSA.java
> java signatureRSA
a68707606edf2c3815ee6afb63dc380ed6bda5e3271a03959682b0082fa466f80e55773c6e7989d7416b5068c9fb92a2f1f418d0b098baa827c3a45994de593acfef4995e79eb6ca46cf83d74ca78ea3153e082ec3332e24098a0620ad6e165ef900152dda332b987525c1c81aa22b70ec6fdf3286aa34b33888dda8c5ba324d2bcc5bdb13c3a586fc5a2fef4a498cdc6f212f71c10db3c81334f0d56723ee7b943e75385138d4469925ca4bb2851f5944662591edf47bc152498306945f5ce11bc129aab2b4aec83945a520997b9412ecda2ce4308719513d239dc5dec47df6e111bf5d7c4a9ea954294b3ea10b43c0919eba618dfeb306f291a0b53c0e492f

Pause the process ... Press any key to resume & exit the application.
```
Aplikasi java `signatureRSA` akan melakukan load private key dari keystore dan melakukan proses digital signature, kemudian aplikasi di pause untuk mensimulasikan bahwa aplikasi masih tetap running sebagai service. Pada proses ini sebenarnya key terload di memory komputer.

Kali ini kita akan menggunakan teknik dump memory untuk menemukan private key. 

Untuk dump memory suatu aplikasi yang sedang running pada Windows, Anda dapat lakukan dengan membuka `Task Manager`. Pada Tab Processes, temukan aplikasi java yang sedang running. Pada enviroment yang kami gunakan bernama `Windows Command Processor > OpenJDK Platform binary`. 

Klik kanan pada `OpenJDK Platform binary` kemudian pilih `Create dump file`. 
File dump akan disimpan ke lokasi folder  temporary. Pada environment yang kami gunakan dump file akan disimpan ke direktori `C:\Users\<username>\AppData\Local\Temp\java.DMP`

Copy java.DMP dan paste di Linux. Simpan disembarang folder misal di folder `tmp`.

Untuk mencari RSA private public key pada dump file dapat menggunakan program `rsakeyfind`. Untuk bisa menggunakan `rsakeyfind` dapat menggunakan 2 cara, yaitu install dengan command berikut:
```
$ sudo apt install rsakeyfind 
```
Atau compile dari source code `rsakeyfind` dengan cara sebagai berikut. Pada Linux, buka folder `rsakeyfind`:
```
$ cd rsakeyfind-1.0/rsakeyfind
```
>**Note**: Program ini juga dapat digunakan pada Windows dengan menggunakan MinGW
>
Compile program `rsakeyfind` dengan menggunakan command:
```
$ make -f Makefile 
```
Jalankan program `rsakeyfind`. Gunakan java.DMP sebagai input. Simpan hasilnya pada file `rsakeyfound`.
```
$ ./rsakeyfind /tmp/java.DMP > rsakeyfound
```
Bila RSA private key ditemukan, hasilnya akan disimpan di dalam file `rsakeyfound`.

Gunakan command `cat` untuk melihat hasilnya.
```
$ cat /tmp/rsakeyfound 
FOUND PRIVATE KEY AT ee5dd86
version = 
00 
modulus = 
00 b2 ba 4e 2f 7c f9 1b 5a 86 7e 1c f4 9b 2f 3c 
fb 1b a2 2c db 68 ee 40 28 b9 d7 f2 72 53 be e8 
0f aa a3 89 7f 9f f7 cd d4 99 c8 35 2e aa 4c 7f 
1b d6 c7 28 07 06 37 26 ca 91 3c a3 77 46 bc ff 
85 3d 40 d8 61 71 7d 5f e9 bc 3d c4 26 4d 6e 18 
dc bc b6 99 b4 11 c4 0c ae 48 b2 2d 19 75 60 5f 
75 35 ec 0d 10 35 db d4 cf b0 a0 9b 7e ae a4 19 
de 9b fd 7a 32 af 43 3b 0c b2 5e d1 14 67 41 8e 
fd 07 12 e4 c0 f3 8d 8d e0 38 63 73 cf da 1a 05 
b7 d0 07 b3 22 7e 6f 8f 7e 5c 8d 9d d9 de f2 88 
96 7c 4b a6 60 4a 02 e7 a7 ab b8 18 bd e9 b5 53 
9d 55 ba 54 22 83 50 01 c8 db bb c6 b1 9c d4 19 
9e 7d 18 23 55 a8 c1 c2 5e db 40 cc 30 0a bd db 
1c ba e4 97 d5 f1 1c d7 09 36 c0 48 b1 d3 62 2f 
97 0b 5e 74 66 e6 12 ac de 73 20 90 23 bc c8 5d 
50 47 8a ca 71 25 91 7f 30 8d fa 98 b7 21 54 64 
05 
publicExponent = 
01 00 01 
privateExponent = 
35 8a ac 7b 7b 73 e0 e6 e8 9d 85 2d 1e 04 33 bf 
82 12 be 5f 68 7a df 4b 94 09 06 3f 8b a9 08 3a 
11 43 76 a0 7e 1a ad 55 c0 50 02 e6 9c eb d7 b4 
4e 4e 51 46 71 6d 3a 38 ba f3 b0 80 bd f7 46 90 
f2 bd a7 54 e1 bb ce 6c e6 10 96 df 66 98 d1 e7 
e6 bc c9 24 f1 a1 8a ae f7 66 31 2e 8c 44 37 b4 
94 57 c7 be 10 74 6f b7 5c 51 70 e3 ad 8b c9 98 
2f a0 ea e7 9e 2b 84 d8 13 e3 65 bd 38 05 a2 c0 
d7 d4 aa a9 7e 0b 7d 51 46 12 42 2c 98 e0 e7 58 
ed 27 9a 01 7a 28 7d d1 d1 7b ba a5 53 0b 34 4d 
19 44 9a 8b 51 07 bd c7 8e d8 36 1a 32 3c c1 fc 
b5 f6 99 1c f4 de ad 9d 9d 23 8d 39 6b fd d1 d7 
77 91 c7 12 3d 75 86 25 ac cc d2 ad 64 19 c0 25 
81 5f c3 6b f4 d4 fe f4 3a f4 7b 17 4e 6e 7a 3b 
3b b4 cb bf 92 a8 e2 69 df 9d f7 5e 4d 22 eb 4c 
ba 08 7e b5 0c 3a e9 68 24 29 95 d6 23 38 6a ad 
prime1 = 
00 e5 11 c2 e0 8c 25 be f1 cc 1e 5e be 72 eb 30 
31 a3 12 fb 35 d0 6c fc 49 bd 2b 45 4d d7 51 9c 
96 9e 6b c5 f6 b6 a0 17 2a 30 9e b3 b6 30 b0 27 
48 f0 7e d7 0f 3e 97 5d 1b 85 3e 22 b7 1c c7 b0 
cc d2 25 eb 66 35 47 04 89 38 1c 2b 90 25 8f 1f 
d6 bc 36 18 52 4f e4 61 b7 16 f4 5a 67 e8 19 94 
f1 17 69 9e 94 16 37 87 da e5 c2 d6 4e 7d 01 1f 
d7 6e f2 f1 5a 02 72 da 04 d2 27 73 5b df ac c6 
47 
prime2 = 
00 c7 bd 6d 0f 16 9d 4a 6a 93 ea 53 65 c7 8c b6 
c0 ef e2 da 41 82 d6 62 9e 00 85 03 ff e9 f1 bf 
70 ba 29 f1 2e 94 1d 8d 62 dc 0c 97 3d 34 94 b8 
35 77 89 15 93 49 c9 06 fd 48 ea 2c b8 c0 dd 10 
e7 8e 2a 51 70 9f cd 92 76 9b 96 3b 7d 3d 60 d9 
ec e7 5d 29 6b 66 45 47 57 64 ac 7e 16 ae 53 ed 
34 22 4c 2d ec ce 92 7d 22 ec 9e bd 6a 52 0e 74 
76 99 56 36 32 5d 2f 21 ae a0 a4 37 b4 d2 8d 8d 
53 
exponent1 = 
00 90 82 23 ee 84 7b 4b 7a 3e ca e9 fa 3b 85 23 
62 2c fe 4c 7c 26 80 9a a7 ea 2e e2 09 b9 4c 42 
fc c1 f4 3b 6e e8 c7 15 d6 07 d6 16 89 59 2d c7 
9a bc 1d 48 8e d5 6d a7 cf 34 bf 7e 27 06 80 f3 
88 43 0a a8 f6 51 a5 fb b6 95 2f 30 4c 3f 65 8b 
30 6c 8b b3 cc 39 9a 5a 0a a6 f0 80 18 0c d4 16 
a2 01 a3 1f e7 4c 1c bc 17 db da 8e 57 f5 84 40 
12 28 6c 1a d6 2d b5 46 d4 c3 bf 1f 87 56 14 29 
01 
exponent2 = 
01 78 d8 eb ea 8b c0 37 a6 dd 6c 3d ec c9 91 b3 
3c 51 5c b0 6c e9 6d 92 a2 88 58 6e 33 68 2f b4 
01 fb 3b 26 cf ba a4 07 1d 7e 0e 43 4a 36 ea 60 
a2 7f e5 23 cf c0 66 87 c9 58 c8 79 98 1a 48 fc 
9b e4 9d f3 09 8a 77 50 de 76 ad fb 8f 4f 3e 6b 
eb 8c 99 38 8c 69 68 4c 31 96 bf db 24 ef c3 68 
c5 f3 ef c6 b6 03 08 5f 10 85 5e fa 00 9e d0 a6 
eb 1c 62 8b de 79 f1 72 9b 06 7a ce 9f 5a a1 b7 
coefficient = 
48 0d 63 4f 23 44 e9 ef 1c 17 c1 77 e2 cf 95 fd 
25 4c eb 76 92 2d e7 8f 1e 26 2a 00 5c 26 eb 42 
42 73 81 24 88 45 8f 54 21 3b 94 98 f0 94 c4 32 
b6 20 9a 01 d8 6a 5b 4b d1 66 3c 4e bc 27 a4 f3 
b4 49 c0 21 11 25 df 0a 94 09 12 42 95 31 ed 76 
cf e8 0d 1e 95 5c da e8 93 33 4b cf 3b 91 55 56 
a7 2e ef 6f 00 22 12 f1 c4 f9 98 ce 52 c9 fa 72 
9f 9b 04 7b a8 21 b2 12 32 8a 77 9f 15 68 2f 03 

...
```
Untuk mengecek kebenaran nilai private key ini, kita perlu mengecek dari private key yang asli yang tersimpan di keystore.

Copy keystore.p12 dari Windows ke Linux. Masukkan ke folder `tmp`.

Karena keystore dalam format PKCS#12, maka kita perlu mengconvert terlebih dahulu ke format PEM. Gunakan command di bawah ini untuk convert:
```
$ openssl pkcs12 -in keystore.p12 -out keystore.pem
Enter Import Password:
Enter PEM pass phrase:
Verifying - Enter PEM pass phrase:
```
Untuk melihat nilai key pada keystore.pem, gunakan command berikut:
```
$ openssl rsa -inform DER -check -text -in keystore.pem
Enter pass phrase for keystore.pem:
RSA Private-Key: (2048 bit, 2 primes)
modulus:
    00:b2:ba:4e:2f:7c:f9:1b:5a:86:7e:1c:f4:9b:2f:
    3c:fb:1b:a2:2c:db:68:ee:40:28:b9:d7:f2:72:53:
    be:e8:0f:aa:a3:89:7f:9f:f7:cd:d4:99:c8:35:2e:
    aa:4c:7f:1b:d6:c7:28:07:06:37:26:ca:91:3c:a3:
    77:46:bc:ff:85:3d:40:d8:61:71:7d:5f:e9:bc:3d:
    c4:26:4d:6e:18:dc:bc:b6:99:b4:11:c4:0c:ae:48:
    b2:2d:19:75:60:5f:75:35:ec:0d:10:35:db:d4:cf:
    b0:a0:9b:7e:ae:a4:19:de:9b:fd:7a:32:af:43:3b:
    0c:b2:5e:d1:14:67:41:8e:fd:07:12:e4:c0:f3:8d:
    8d:e0:38:63:73:cf:da:1a:05:b7:d0:07:b3:22:7e:
    6f:8f:7e:5c:8d:9d:d9:de:f2:88:96:7c:4b:a6:60:
    4a:02:e7:a7:ab:b8:18:bd:e9:b5:53:9d:55:ba:54:
    22:83:50:01:c8:db:bb:c6:b1:9c:d4:19:9e:7d:18:
    23:55:a8:c1:c2:5e:db:40:cc:30:0a:bd:db:1c:ba:
    e4:97:d5:f1:1c:d7:09:36:c0:48:b1:d3:62:2f:97:
    0b:5e:74:66:e6:12:ac:de:73:20:90:23:bc:c8:5d:
    50:47:8a:ca:71:25:91:7f:30:8d:fa:98:b7:21:54:
    64:05
publicExponent: 65537 (0x10001)
privateExponent:
    35:8a:ac:7b:7b:73:e0:e6:e8:9d:85:2d:1e:04:33:
    bf:82:12:be:5f:68:7a:df:4b:94:09:06:3f:8b:a9:
    08:3a:11:43:76:a0:7e:1a:ad:55:c0:50:02:e6:9c:
    eb:d7:b4:4e:4e:51:46:71:6d:3a:38:ba:f3:b0:80:
    bd:f7:46:90:f2:bd:a7:54:e1:bb:ce:6c:e6:10:96:
    df:66:98:d1:e7:e6:bc:c9:24:f1:a1:8a:ae:f7:66:
    31:2e:8c:44:37:b4:94:57:c7:be:10:74:6f:b7:5c:
    51:70:e3:ad:8b:c9:98:2f:a0:ea:e7:9e:2b:84:d8:
    13:e3:65:bd:38:05:a2:c0:d7:d4:aa:a9:7e:0b:7d:
    51:46:12:42:2c:98:e0:e7:58:ed:27:9a:01:7a:28:
    7d:d1:d1:7b:ba:a5:53:0b:34:4d:19:44:9a:8b:51:
    07:bd:c7:8e:d8:36:1a:32:3c:c1:fc:b5:f6:99:1c:
    f4:de:ad:9d:9d:23:8d:39:6b:fd:d1:d7:77:91:c7:
    12:3d:75:86:25:ac:cc:d2:ad:64:19:c0:25:81:5f:
    c3:6b:f4:d4:fe:f4:3a:f4:7b:17:4e:6e:7a:3b:3b:
    b4:cb:bf:92:a8:e2:69:df:9d:f7:5e:4d:22:eb:4c:
    ba:08:7e:b5:0c:3a:e9:68:24:29:95:d6:23:38:6a:
    ad
prime1:
    00:e5:11:c2:e0:8c:25:be:f1:cc:1e:5e:be:72:eb:
    30:31:a3:12:fb:35:d0:6c:fc:49:bd:2b:45:4d:d7:
    51:9c:96:9e:6b:c5:f6:b6:a0:17:2a:30:9e:b3:b6:
    30:b0:27:48:f0:7e:d7:0f:3e:97:5d:1b:85:3e:22:
    b7:1c:c7:b0:cc:d2:25:eb:66:35:47:04:89:38:1c:
    2b:90:25:8f:1f:d6:bc:36:18:52:4f:e4:61:b7:16:
    f4:5a:67:e8:19:94:f1:17:69:9e:94:16:37:87:da:
    e5:c2:d6:4e:7d:01:1f:d7:6e:f2:f1:5a:02:72:da:
    04:d2:27:73:5b:df:ac:c6:47
prime2:
    00:c7:bd:6d:0f:16:9d:4a:6a:93:ea:53:65:c7:8c:
    b6:c0:ef:e2:da:41:82:d6:62:9e:00:85:03:ff:e9:
    f1:bf:70:ba:29:f1:2e:94:1d:8d:62:dc:0c:97:3d:
    34:94:b8:35:77:89:15:93:49:c9:06:fd:48:ea:2c:
    b8:c0:dd:10:e7:8e:2a:51:70:9f:cd:92:76:9b:96:
    3b:7d:3d:60:d9:ec:e7:5d:29:6b:66:45:47:57:64:
    ac:7e:16:ae:53:ed:34:22:4c:2d:ec:ce:92:7d:22:
    ec:9e:bd:6a:52:0e:74:76:99:56:36:32:5d:2f:21:
    ae:a0:a4:37:b4:d2:8d:8d:53
exponent1:
    00:90:82:23:ee:84:7b:4b:7a:3e:ca:e9:fa:3b:85:
    23:62:2c:fe:4c:7c:26:80:9a:a7:ea:2e:e2:09:b9:
    4c:42:fc:c1:f4:3b:6e:e8:c7:15:d6:07:d6:16:89:
    59:2d:c7:9a:bc:1d:48:8e:d5:6d:a7:cf:34:bf:7e:
    27:06:80:f3:88:43:0a:a8:f6:51:a5:fb:b6:95:2f:
    30:4c:3f:65:8b:30:6c:8b:b3:cc:39:9a:5a:0a:a6:
    f0:80:18:0c:d4:16:a2:01:a3:1f:e7:4c:1c:bc:17:
    db:da:8e:57:f5:84:40:12:28:6c:1a:d6:2d:b5:46:
    d4:c3:bf:1f:87:56:14:29:01
exponent2:
    01:78:d8:eb:ea:8b:c0:37:a6:dd:6c:3d:ec:c9:91:
    b3:3c:51:5c:b0:6c:e9:6d:92:a2:88:58:6e:33:68:
    2f:b4:01:fb:3b:26:cf:ba:a4:07:1d:7e:0e:43:4a:
    36:ea:60:a2:7f:e5:23:cf:c0:66:87:c9:58:c8:79:
    98:1a:48:fc:9b:e4:9d:f3:09:8a:77:50:de:76:ad:
    fb:8f:4f:3e:6b:eb:8c:99:38:8c:69:68:4c:31:96:
    bf:db:24:ef:c3:68:c5:f3:ef:c6:b6:03:08:5f:10:
    85:5e:fa:00:9e:d0:a6:eb:1c:62:8b:de:79:f1:72:
    9b:06:7a:ce:9f:5a:a1:b7
coefficient:
    48:0d:63:4f:23:44:e9:ef:1c:17:c1:77:e2:cf:95:
    fd:25:4c:eb:76:92:2d:e7:8f:1e:26:2a:00:5c:26:
    eb:42:42:73:81:24:88:45:8f:54:21:3b:94:98:f0:
    94:c4:32:b6:20:9a:01:d8:6a:5b:4b:d1:66:3c:4e:
    bc:27:a4:f3:b4:49:c0:21:11:25:df:0a:94:09:12:
    42:95:31:ed:76:cf:e8:0d:1e:95:5c:da:e8:93:33:
    4b:cf:3b:91:55:56:a7:2e:ef:6f:00:22:12:f1:c4:
    f9:98:ce:52:c9:fa:72:9f:9b:04:7b:a8:21:b2:12:
    32:8a:77:9f:15:68:2f:03
RSA key ok
writing RSA key
-----BEGIN PRIVATE KEY-----
MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCyuk4vfPkbWoZ+
HPSbLzz7G6Is22juQCi51/JyU77oD6qjiX+f983Umcg1LqpMfxvWxygHBjcmypE8
o3dGvP+FPUDYYXF9X+m8PcQmTW4Y3Ly2mbQRxAyuSLItGXVgX3U17A0QNdvUz7Cg
m36upBnem/16Mq9DOwyyXtEUZ0GO/QcS5MDzjY3gOGNzz9oaBbfQB7Mifm+PflyN
ndne8oiWfEumYEoC56eruBi96bVTnVW6VCKDUAHI27vGsZzUGZ59GCNVqMHCXttA
zDAKvdscuuSX1fEc1wk2wEix02IvlwtedGbmEqzecyCQI7zIXVBHispxJZF/MI36
mLchVGQFAgMBAAECggEANYqse3tz4ObonYUtHgQzv4ISvl9oet9LlAkGP4upCDoR
Q3agfhqtVcBQAuac69e0Tk5RRnFtOji687CAvfdGkPK9p1Thu85s5hCW32aY0efm
vMkk8aGKrvdmMS6MRDe0lFfHvhB0b7dcUXDjrYvJmC+g6ueeK4TYE+NlvTgFosDX
1Kqpfgt9UUYSQiyY4OdY7SeaAXoofdHRe7qlUws0TRlEmotRB73Hjtg2GjI8wfy1
9pkc9N6tnZ0jjTlr/dHXd5HHEj11hiWszNKtZBnAJYFfw2v01P70OvR7F05uejs7
tMu/kqjiad+d915NIutMugh+tQw66WgkKZXWIzhqrQKBgQDlEcLgjCW+8cweXr5y
6zAxoxL7NdBs/Em9K0VN11Gclp5rxfa2oBcqMJ6ztjCwJ0jwftcPPpddG4U+Ircc
x7DM0iXrZjVHBIk4HCuQJY8f1rw2GFJP5GG3FvRaZ+gZlPEXaZ6UFjeH2uXC1k59
AR/XbvLxWgJy2gTSJ3Nb36zGRwKBgQDHvW0PFp1KapPqU2XHjLbA7+LaQYLWYp4A
hQP/6fG/cLop8S6UHY1i3AyXPTSUuDV3iRWTSckG/UjqLLjA3RDnjipRcJ/Nknab
ljt9PWDZ7OddKWtmRUdXZKx+Fq5T7TQiTC3szpJ9IuyevWpSDnR2mVY2Ml0vIa6g
pDe00o2NUwKBgQCQgiPuhHtLej7K6fo7hSNiLP5MfCaAmqfqLuIJuUxC/MH0O27o
xxXWB9YWiVktx5q8HUiO1W2nzzS/ficGgPOIQwqo9lGl+7aVLzBMP2WLMGyLs8w5
mloKpvCAGAzUFqIBox/nTBy8F9vajlf1hEASKGwa1i21RtTDvx+HVhQpAQKBgAF4
2Ovqi8A3pt1sPezJkbM8UVywbOltkqKIWG4zaC+0Afs7Js+6pAcdfg5DSjbqYKJ/
5SPPwGaHyVjIeZgaSPyb5J3zCYp3UN52rfuPTz5r64yZOIxpaEwxlr/bJO/DaMXz
78a2AwhfEIVe+gCe0KbrHGKL3nnxcpsGes6fWqG3AoGASA1jTyNE6e8cF8F34s+V
/SVM63aSLeePHiYqAFwm60JCc4EkiEWPVCE7lJjwlMQytiCaAdhqW0vRZjxOvCek
87RJwCERJd8KlAkSQpUx7XbP6A0elVza6JMzS887kVVWpy7vbwAiEvHE+ZjOUsn6
cp+bBHuoIbISMop3nxVoLwM=
-----END PRIVATE KEY-----
```
Bandingkan nilai key tersebut dengan nilai key pada file `rsakeyfound`. Jika nilai key nya sama, berarti private key dan public key hasil dari dump file berhasil ditemukan.
<br>

## 3. Menemukan RSA Private Key pada Aplikasi Digital Signature Golang yang Running di Windows
Copy folder `finding-key-memory` ke Windows.

Gunakan command prompt, buka folder `golang-rsa`.
```
> cd finding-key-memory\golang-rsa
```
Compile program signRSA.go, kemudian jalankan executable programnya
```
> go build signRSA.go
> signRSA.exe
9c6fcf613ce336d74be337f90d1b581ae388a776f80c2c069ff40ce2eabeae9ed5435fb9db65e9bf28cd2a42f25f82bf16378dedc36439d9f7408573754cc04dcf88c6b5f1a3ba074a38d9053c9073f5910a1d0ae6381b1d236e19f19f64a6eddb26814b134f83c684c1d3682b2d8b3a2ef3b707fbbad7699ace8e2231d43dda75c2f3db74ee79ed3d6dcd5cd6e2ed04bcdb190cc088d19f0c7ae053c11e2f93839ffa6760196a69cc85de27add056b204a20e25458dac53ccbae0fc591b6f9eea0f13d319d56475b4b71daef62e28474ac270405dddfb1a5d1a1f124c641f12c1b5bb8fab7b3dee5e6d3da6e4a5c2571ef1f3e2e90d213b92fae648f6cac74c

Pause the process ... Press any key to resume & exit the application.
```
Program ini akan melakukan generate random RSA key 2048 bit, kemudian menyimpannya ke dalam file. Setelah itu program akan load key dari private.pem dan melakukan digital signature terhadap suatu data. Kemudian aplikasi di pause untuk mensimulasikan bahwa aplikasi masih tetap running sebagai service. Pada proses ini sebenarnya key terload di memory komputer.

Kembali kita akan menggunakan teknik dump memory untuk menemukan private key. 

Untuk dump memory suatu aplikasi yang sedang running pada Windows, Anda dapat lakukan dengan membuka `Task Manager`. Pada Tab Processes, temukan aplikasi `signRSA` yang sedang running. Pada enviroment yang kami gunakan bernama `Windows Command Processor > signRSA`. 

Klik kanan pada `signRSA` kemudian pilih `Create dump file`. 
File dump akan disimpan ke lokasi folder temporary. Pada environment yang kami gunakan disimpan ke: 
```
C:\Users\<username>\AppData\Local\Temp\signRSA.DMP
```
Copy signRSA.DMP dan paste di Linux. Simpan di sembarang folder misal di folder `tmp`.

Untuk mencari RSA private public key pada dump file dapat menggunakan program `rsakeyfind`. Untuk bisa menggunakan `rsakeyfind` dapat menggunakan 2 cara, yaitu install dengan command berikut:
```
$ sudo apt install rsakeyfind 
```
Atau compile dari source code `rsakeyfind` dengan cara sebagai berikut. Pada Linux, buka folder `rsakeyfind`:
```
$ cd rsakeyfind-1.0/rsakeyfind
```
>**Note**: Program ini juga dapat digunakan pada Windows dengan menggunakan MinGW

Compile program `rsakeyfind` dengan menggunakan command:
```
$ make -f Makefile 
```
Jalankan program `rsakeyfind`. Gunakan signRSA.DMP sebagai input. Dan simpan hasilnya ke file `rsakeyfound`.
```
$ ./rsakeyfind /tmp/signRSA.DMP > rsakeyfound
```
Bila RSA private key ditemukan, hasilnya akan disimpan di dalam file `rsakeyfound`.

Gunakan command `cat` untuk melihat hasilnya:
```
$ cat /tmp/signRSA.DMP 
FOUND PRIVATE KEY AT 4d1772
version = 
00 
modulus = 
00 e5 a9 09 53 ff c6 d5 fe 72 4c bc 7a b9 70 ba 
78 3b ed af d6 c2 0e 5e 6a 91 59 40 61 d6 c0 0b 
d3 6d 6a 82 86 67 b7 26 c2 b2 69 ec b1 1d 32 48 
b2 5d e2 e4 99 f2 bd 9c 63 9f ed dd f3 3e da 21 
2c 4d b7 3e 10 a7 59 f0 21 15 46 9f 29 fa f2 dd 
37 18 40 a5 91 26 06 a0 9b 79 65 b6 dc 1b c1 55 
a4 ed 2f ea b0 03 f0 2a ab 99 74 5c d1 a9 91 17 
10 19 53 1e 9a 85 6e 4e 1d 71 46 ac 2a da 2a 70 
cd bc 2f 1b f6 87 f2 d6 03 10 2a 0b 76 c4 d7 af 
56 60 47 2f db c1 fe 50 a2 9e 7b e4 94 3a 90 d7 
40 cf 09 d5 05 f1 eb d1 6c b8 2b 10 98 69 67 dc 
23 c8 c2 f8 d5 b4 1d 3a f2 69 95 86 5c 6f 7f 49 
c3 46 7e 91 38 8d 9a 73 5b cd 4b 33 22 ba 25 9a 
c0 bc b4 61 03 59 54 1b 5c c6 ea f9 e9 73 5b 87 
62 6b c6 3d 7d e4 e2 c3 b8 14 8a 5b f2 55 48 31 
fa 4a 1a 49 63 a4 dd 90 ba 65 5e 01 e5 09 cd a2 
bb 
publicExponent = 
01 00 01 
privateExponent = 
3d af 98 8c d2 5f d2 ec c4 40 ae 43 7c 79 b5 ec 
43 94 85 2d 76 f4 2b 12 0e b6 5d 5f aa 31 ed 3b 
3d e1 64 eb bb 01 eb cc 51 e5 b7 a8 9f 01 f9 9a 
c7 33 01 5a 04 64 3f 94 56 c9 aa 5b 02 9f ce 57 
3b 0c a4 04 0f 1f 4e 2b 64 ae 92 63 d1 3e d8 82 
4a e6 5a 52 51 57 68 5e 3a 4e 42 be 7e 4a 00 ba 
0a e7 5a ef 2a 2f a1 16 74 62 57 e8 0c d6 a6 ce 
31 2f 2d 42 2d e2 c1 e2 90 a1 7d ef 08 a8 6b a1 
a4 a6 40 3a d4 62 83 bc 95 42 78 11 ea cc f8 71 
c2 f4 96 0a 78 c1 0c a7 a7 0f 1c 9b e2 8e ff 9e 
93 ed 01 b9 92 36 4f 98 f7 8b d6 6e f3 4f 6d d7 
34 da 23 cf a9 fd db ef 96 ad 72 e9 53 1d 56 ae 
08 a8 69 8f 48 9b be 15 ec a2 04 74 dd 4c 3a 61 
79 33 b6 27 9b d8 ba b6 fb 04 a1 ac 88 47 2b e7 
04 7a f3 13 3f ad 23 39 2d b7 1d e3 00 79 91 45 
06 81 8f 03 ef dd 94 64 00 cf 0f 3f a6 33 0a c9 
prime1 = 
00 fe d7 4f 81 4f 62 97 68 bc c2 73 2e e2 83 bc 
86 d4 05 05 e0 00 e9 db 81 52 ff 5b bc 71 10 7e 
75 45 de 02 49 96 41 84 67 4f 59 6c ca 9e 02 18 
bb 16 9a b6 94 76 53 c9 d0 90 74 90 a4 36 8b 69 
68 f1 82 f6 48 19 da 2f 7c 68 25 01 ea 40 0e 70 
80 31 bc 59 ba 21 86 0f d1 fa 47 be 8a c2 d3 02 
95 ca 0f 1a b3 d0 e5 7f 77 cd 90 1b 5f 18 cd 56 
fa 0b 52 26 1b ab ee 1e 02 34 3a ca c2 10 57 d9 
55 
prime2 = 
00 e6 b4 68 fb 96 cf bc c6 b9 be 2a 15 43 0a 54 
10 42 20 ba d0 e9 e1 42 a4 24 65 fb b4 11 2f 74 
cb e4 37 5c 60 6b 15 1f 2d 79 27 4c 5c 2c 8a 00 
91 46 79 dd 45 72 77 ed fa 26 c5 ed 42 47 4f bb 
98 eb 86 75 59 9f df c1 4f 86 8a 6b d0 e4 10 55 
a3 5e 51 b9 6e cc 09 57 da 78 3c 5f b9 95 79 1d 
78 00 24 b5 8a 26 99 45 43 b5 d5 98 5e 8e 6b d9 
d0 79 8e 56 21 76 de b8 50 4e fe 4d f1 b9 b5 4b 
cf 
exponent1 = 
00 9d d6 9f 80 98 b9 98 cb 9f 35 d4 7f 72 7e 63 
73 22 b8 65 b6 22 fa b5 20 b9 56 3d ec 4e 1f 08 
23 fc 7d 0f ef a1 e7 bc 68 fc 45 87 d3 4a e0 43 
8f 04 e6 18 35 85 c0 49 9c 2b 0c 77 55 ae 0e d4 
df d1 cd ea 5a 27 e0 f2 4e 5c 76 a7 63 fd fe b3 
81 42 cc c4 ca 3a df 3c c7 31 fb e3 b1 30 0f df 
67 04 fa 2b f0 b9 6e 9b 6f 89 5b 3b 08 c0 64 d5 
fd 12 ab f3 bc 69 d5 96 b2 88 c4 ed 36 1b 47 3d 
ed 
exponent2 = 
5d 02 e3 48 7b 76 e9 4e dd 57 d3 b3 9a c1 b1 3e 
94 ca 89 46 ec e4 0a 70 0e d4 a6 f3 e3 f1 d4 0e 
d7 c5 92 12 b5 59 60 a6 7e af df 53 52 09 99 4c 
f8 b2 ed 08 b0 ab b5 60 22 fd ad 38 09 74 15 d8 
03 2c c4 67 0b d3 b4 26 11 c2 00 d1 c9 00 e4 e5 
e6 82 e0 55 d1 20 fb 30 73 45 33 fa a8 4e 9a 18 
e5 45 e7 82 f9 28 d6 0a 04 67 46 f4 e8 4a 87 18 
4b c9 9c e4 04 fe aa 24 86 94 74 6c 3e 48 df 75 
coefficient = 
00 ad c4 f9 b5 bf d5 10 d2 25 e7 fb 2a 82 60 cb 
f4 37 04 2a 5d fd 9d 07 ac c1 83 7a bf 93 01 b7 
7d 5b 4d 90 5c e9 4e 9d b1 86 37 a3 67 b8 78 5e 
d6 09 09 2f 01 a4 52 01 d5 52 8d 13 1e 26 a5 92 
c9 f1 b9 2b 7b d9 76 4e 4a a5 06 d2 7a f4 42 2d 
2e fc 33 60 e8 64 9b ba 7e 8e 2f ba f8 9f 44 0c 
74 7a 7e 28 2a c5 36 e6 d2 ea 01 e8 87 cf d9 8a 
d0 c7 c5 24 4d 0b bd 31 61 5c af 8f 08 19 cc 98 
b8 

...
```
Untuk mengecek kebenaran nilai private key ini, kita perlu mengecek dari private key yang asli yang tersimpan di private.pem.

Copy private.pem dari Windows ke Linux. Masukkan ke folder `tmp`.

Untuk melihat nilai key pada private.pem, gunakan command berikut:
```
$ openssl rsa -inform DER -check -text -in private.pem 
RSA Private-Key: (2048 bit, 2 primes)
modulus:
    00:e5:a9:09:53:ff:c6:d5:fe:72:4c:bc:7a:b9:70:
    ba:78:3b:ed:af:d6:c2:0e:5e:6a:91:59:40:61:d6:
    c0:0b:d3:6d:6a:82:86:67:b7:26:c2:b2:69:ec:b1:
    1d:32:48:b2:5d:e2:e4:99:f2:bd:9c:63:9f:ed:dd:
    f3:3e:da:21:2c:4d:b7:3e:10:a7:59:f0:21:15:46:
    9f:29:fa:f2:dd:37:18:40:a5:91:26:06:a0:9b:79:
    65:b6:dc:1b:c1:55:a4:ed:2f:ea:b0:03:f0:2a:ab:
    99:74:5c:d1:a9:91:17:10:19:53:1e:9a:85:6e:4e:
    1d:71:46:ac:2a:da:2a:70:cd:bc:2f:1b:f6:87:f2:
    d6:03:10:2a:0b:76:c4:d7:af:56:60:47:2f:db:c1:
    fe:50:a2:9e:7b:e4:94:3a:90:d7:40:cf:09:d5:05:
    f1:eb:d1:6c:b8:2b:10:98:69:67:dc:23:c8:c2:f8:
    d5:b4:1d:3a:f2:69:95:86:5c:6f:7f:49:c3:46:7e:
    91:38:8d:9a:73:5b:cd:4b:33:22:ba:25:9a:c0:bc:
    b4:61:03:59:54:1b:5c:c6:ea:f9:e9:73:5b:87:62:
    6b:c6:3d:7d:e4:e2:c3:b8:14:8a:5b:f2:55:48:31:
    fa:4a:1a:49:63:a4:dd:90:ba:65:5e:01:e5:09:cd:
    a2:bb
publicExponent: 65537 (0x10001)
privateExponent:
    3d:af:98:8c:d2:5f:d2:ec:c4:40:ae:43:7c:79:b5:
    ec:43:94:85:2d:76:f4:2b:12:0e:b6:5d:5f:aa:31:
    ed:3b:3d:e1:64:eb:bb:01:eb:cc:51:e5:b7:a8:9f:
    01:f9:9a:c7:33:01:5a:04:64:3f:94:56:c9:aa:5b:
    02:9f:ce:57:3b:0c:a4:04:0f:1f:4e:2b:64:ae:92:
    63:d1:3e:d8:82:4a:e6:5a:52:51:57:68:5e:3a:4e:
    42:be:7e:4a:00:ba:0a:e7:5a:ef:2a:2f:a1:16:74:
    62:57:e8:0c:d6:a6:ce:31:2f:2d:42:2d:e2:c1:e2:
    90:a1:7d:ef:08:a8:6b:a1:a4:a6:40:3a:d4:62:83:
    bc:95:42:78:11:ea:cc:f8:71:c2:f4:96:0a:78:c1:
    0c:a7:a7:0f:1c:9b:e2:8e:ff:9e:93:ed:01:b9:92:
    36:4f:98:f7:8b:d6:6e:f3:4f:6d:d7:34:da:23:cf:
    a9:fd:db:ef:96:ad:72:e9:53:1d:56:ae:08:a8:69:
    8f:48:9b:be:15:ec:a2:04:74:dd:4c:3a:61:79:33:
    b6:27:9b:d8:ba:b6:fb:04:a1:ac:88:47:2b:e7:04:
    7a:f3:13:3f:ad:23:39:2d:b7:1d:e3:00:79:91:45:
    06:81:8f:03:ef:dd:94:64:00:cf:0f:3f:a6:33:0a:
    c9
prime1:
    00:fe:d7:4f:81:4f:62:97:68:bc:c2:73:2e:e2:83:
    bc:86:d4:05:05:e0:00:e9:db:81:52:ff:5b:bc:71:
    10:7e:75:45:de:02:49:96:41:84:67:4f:59:6c:ca:
    9e:02:18:bb:16:9a:b6:94:76:53:c9:d0:90:74:90:
    a4:36:8b:69:68:f1:82:f6:48:19:da:2f:7c:68:25:
    01:ea:40:0e:70:80:31:bc:59:ba:21:86:0f:d1:fa:
    47:be:8a:c2:d3:02:95:ca:0f:1a:b3:d0:e5:7f:77:
    cd:90:1b:5f:18:cd:56:fa:0b:52:26:1b:ab:ee:1e:
    02:34:3a:ca:c2:10:57:d9:55
prime2:
    00:e6:b4:68:fb:96:cf:bc:c6:b9:be:2a:15:43:0a:
    54:10:42:20:ba:d0:e9:e1:42:a4:24:65:fb:b4:11:
    2f:74:cb:e4:37:5c:60:6b:15:1f:2d:79:27:4c:5c:
    2c:8a:00:91:46:79:dd:45:72:77:ed:fa:26:c5:ed:
    42:47:4f:bb:98:eb:86:75:59:9f:df:c1:4f:86:8a:
    6b:d0:e4:10:55:a3:5e:51:b9:6e:cc:09:57:da:78:
    3c:5f:b9:95:79:1d:78:00:24:b5:8a:26:99:45:43:
    b5:d5:98:5e:8e:6b:d9:d0:79:8e:56:21:76:de:b8:
    50:4e:fe:4d:f1:b9:b5:4b:cf
exponent1:
    00:9d:d6:9f:80:98:b9:98:cb:9f:35:d4:7f:72:7e:
    63:73:22:b8:65:b6:22:fa:b5:20:b9:56:3d:ec:4e:
    1f:08:23:fc:7d:0f:ef:a1:e7:bc:68:fc:45:87:d3:
    4a:e0:43:8f:04:e6:18:35:85:c0:49:9c:2b:0c:77:
    55:ae:0e:d4:df:d1:cd:ea:5a:27:e0:f2:4e:5c:76:
    a7:63:fd:fe:b3:81:42:cc:c4:ca:3a:df:3c:c7:31:
    fb:e3:b1:30:0f:df:67:04:fa:2b:f0:b9:6e:9b:6f:
    89:5b:3b:08:c0:64:d5:fd:12:ab:f3:bc:69:d5:96:
    b2:88:c4:ed:36:1b:47:3d:ed
exponent2:
    5d:02:e3:48:7b:76:e9:4e:dd:57:d3:b3:9a:c1:b1:
    3e:94:ca:89:46:ec:e4:0a:70:0e:d4:a6:f3:e3:f1:
    d4:0e:d7:c5:92:12:b5:59:60:a6:7e:af:df:53:52:
    09:99:4c:f8:b2:ed:08:b0:ab:b5:60:22:fd:ad:38:
    09:74:15:d8:03:2c:c4:67:0b:d3:b4:26:11:c2:00:
    d1:c9:00:e4:e5:e6:82:e0:55:d1:20:fb:30:73:45:
    33:fa:a8:4e:9a:18:e5:45:e7:82:f9:28:d6:0a:04:
    67:46:f4:e8:4a:87:18:4b:c9:9c:e4:04:fe:aa:24:
    86:94:74:6c:3e:48:df:75
coefficient:
    00:ad:c4:f9:b5:bf:d5:10:d2:25:e7:fb:2a:82:60:
    cb:f4:37:04:2a:5d:fd:9d:07:ac:c1:83:7a:bf:93:
    01:b7:7d:5b:4d:90:5c:e9:4e:9d:b1:86:37:a3:67:
    b8:78:5e:d6:09:09:2f:01:a4:52:01:d5:52:8d:13:
    1e:26:a5:92:c9:f1:b9:2b:7b:d9:76:4e:4a:a5:06:
    d2:7a:f4:42:2d:2e:fc:33:60:e8:64:9b:ba:7e:8e:
    2f:ba:f8:9f:44:0c:74:7a:7e:28:2a:c5:36:e6:d2:
    ea:01:e8:87:cf:d9:8a:d0:c7:c5:24:4d:0b:bd:31:
    61:5c:af:8f:08:19:cc:98:b8
RSA key ok
writing RSA key
-----BEGIN PRIVATE KEY-----
MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDlqQlT/8bV/nJM
vHq5cLp4O+2v1sIOXmqRWUBh1sAL021qgoZntybCsmnssR0ySLJd4uSZ8r2cY5/t
3fM+2iEsTbc+EKdZ8CEVRp8p+vLdNxhApZEmBqCbeWW23BvBVaTtL+qwA/Aqq5l0
XNGpkRcQGVMemoVuTh1xRqwq2ipwzbwvG/aH8tYDECoLdsTXr1ZgRy/bwf5Qop57
5JQ6kNdAzwnVBfHr0Wy4KxCYaWfcI8jC+NW0HTryaZWGXG9/ScNGfpE4jZpzW81L
MyK6JZrAvLRhA1lUG1zG6vnpc1uHYmvGPX3k4sO4FIpb8lVIMfpKGkljpN2QumVe
AeUJzaK7AgMBAAECggEAPa+YjNJf0uzEQK5DfHm17EOUhS129CsSDrZdX6ox7Ts9
4WTruwHrzFHlt6ifAfmaxzMBWgRkP5RWyapbAp/OVzsMpAQPH04rZK6SY9E+2IJK
5lpSUVdoXjpOQr5+SgC6Cuda7yovoRZ0YlfoDNamzjEvLUIt4sHikKF97wioa6Gk
pkA61GKDvJVCeBHqzPhxwvSWCnjBDKenDxyb4o7/npPtAbmSNk+Y94vWbvNPbdc0
2iPPqf3b75atculTHVauCKhpj0ibvhXsogR03Uw6YXkztieb2Lq2+wShrIhHK+cE
evMTP60jOS23HeMAeZFFBoGPA+/dlGQAzw8/pjMKyQKBgQD+10+BT2KXaLzCcy7i
g7yG1AUF4ADp24FS/1u8cRB+dUXeAkmWQYRnT1lsyp4CGLsWmraUdlPJ0JB0kKQ2
i2lo8YL2SBnaL3xoJQHqQA5wgDG8Wbohhg/R+ke+isLTApXKDxqz0OV/d82QG18Y
zVb6C1ImG6vuHgI0OsrCEFfZVQKBgQDmtGj7ls+8xrm+KhVDClQQQiC60OnhQqQk
Zfu0ES90y+Q3XGBrFR8teSdMXCyKAJFGed1Fcnft+ibF7UJHT7uY64Z1WZ/fwU+G
imvQ5BBVo15RuW7MCVfaeDxfuZV5HXgAJLWKJplFQ7XVmF6Oa9nQeY5WIXbeuFBO
/k3xubVLzwKBgQCd1p+AmLmYy5811H9yfmNzIrhltiL6tSC5Vj3sTh8II/x9D++h
57xo/EWH00rgQ48E5hg1hcBJnCsMd1WuDtTf0c3qWifg8k5cdqdj/f6zgULMxMo6
3zzHMfvjsTAP32cE+ivwuW6bb4lbOwjAZNX9EqvzvGnVlrKIxO02G0c97QKBgF0C
40h7dulO3VfTs5rBsT6UyolG7OQKcA7UpvPj8dQO18WSErVZYKZ+r99TUgmZTPiy
7Qiwq7VgIv2tOAl0FdgDLMRnC9O0JhHCANHJAOTl5oLgVdEg+zBzRTP6qE6aGOVF
54L5KNYKBGdG9OhKhxhLyZzkBP6qJIaUdGw+SN91AoGBAK3E+bW/1RDSJef7KoJg
y/Q3BCpd/Z0HrMGDer+TAbd9W02QXOlOnbGGN6NnuHhe1gkJLwGkUgHVUo0THial
ksnxuSt72XZOSqUG0nr0Qi0u/DNg6GSbun6OL7r4n0QMdHp+KCrFNubS6gHoh8/Z
itDHxSRNC70xYVyvjwgZzJi4
-----END PRIVATE KEY-----
```
Bandingkan nilai key tersebut dengan nilai key pada file `rsakeyfound`. Jika sama, berarti private key dan public key hasil dari dump file berhasil ditemukan.
<br>

## 4. Menemukan AES Secret Key pada Aplikasi Enkripsi Golang yang Running di Linux
Copy folder `finding-key-memory` ke Linux.

Gunakan terminal, buka folder `golang-aes`.
```
$ cd finding-key-memory/golang-aes
```
Compile program encAES.go dan running executable programnya menggunakan command berikut:
```
$ go build encAES.go 
$ ./encAES
5918a76e9a157a10a1d52103fc137433fd1818c53ed5e2fdcd6138cc2578a670

Pause the process ... Press any key to resume & exit the application.
``` 
Aplikasi `encAES` akan melakukan load secret key dari key file dan melakukan proses enkripsi, kemudian aplikasi di pause untuk mensimulasikan bahwa aplikasi masih tetap running sebagai service. Pada proses ini sebenarnya key terload di memory komputer.

Untuk mencari AES secret key pada dump file dapat menggunakan program `aeskeyfind`. Untuk bisa menggunakan `aeskeyfind` dapat menggunakan 2 cara, yaitu install dengan command berikut:
```
$ sudo apt install aeskeyfind 
```
Atau compile dari source code `aeskeyfind` dengan cara sebagai berikut. Pada Linux, buka folder `aeskeyfind`.
```
$ cd aeskeyfind-1.0/aeskeyfind
```
>**Note**: Program ini juga dapat digunakan pada Windows dengan menggunakan MinGW
>
Compile program `aeskeyfind` menggunakan command berikut:
```
$ make -f Makefile 
```
Setelah di compile akan menghasilkan file `aeskeyfind` executable. 

Kita akan menggunakan teknik dump memory. Salah satu cara dump memory di linux adalah menggunakan command `gcore`. Untuk menggunakan `gcore` perlu menginstal terlebih dahulu menggunakan command:
```
$ sudo apt install gdb 
```
Jalankan command `ps` untuk melihat nama proses atau PID nya:
```
$ ps -a
2873 pts/0    00:00:00 encAES
2993 pts/1    00:00:00 ps
```
Akan terlihat proses aplikasi `encAES` dan PID nya. Pada contoh ini PID nya adalah 2873.

Jalankan program `gcore` untuk dump memory spesifik pada proses `encAES`. 
```
$ sudo gcore
usage:  gcore [-a] [-o filename] pid
$ sudo gcore 2873
Saved corefile core.2873
```
Hasil dump memory akan disimpan pada file core.2873

Selanjutnya gunakan program `aeskeyfind` untuk menemukan AES secret key pada dump file core.2873
```
$ ./aeskeyfind core.2873 
6368616e676520746869732070617373
Keyfind progress: 100%
```
Terlihat bahwa ditemukan suatu key bernilai `6368616e676520746869732070617373`.

Untuk membuktikan bahwa key yang ditemukan adalah benar, kembali ke terminal pada aplikasi `golang-aes` dan buka file 
`secretkey`.
```
$ cat secretkey 
6368616e676520746869732070617373
```
Dapat kita lihat bahwa nilai key pada file `secretkey` sama dengan nilai key yang ditemukan program `aeskeyfind`. Hal ini menunjukkan bahwa AES clear key dapat ditemukan.
<br>

## Kesimpulan
* Hasil percobaan di atas merupakan contoh saja bagaimana clear key enkripsi dapat ditemukan pada memory komputer. 
* Teknik mendapatkan clear key dari memory dapat dilakukan dengan beragam cara dan tools. 
* Algoritma yang dapat dicari oleh program pada percobaan ini masih terbatas pada AES dan RSA. Tidak tidak menutup kemungkinan proses mendapatkan clear key ini bisa dilakukan pada algoritma-algoritma kriptografi lain.
* Demo tutorial ini dapat anda saksikan pada [video](https://youtu.be/weyonjnS1yw) berikut. 
<br>

## Solusi
Sangat direkomendasikan untuk melakukan proses kriptografi dan proteksi key enkripsi menggunakan hardware khusus kriptografi tersertifikasi yaitu HSM (Hardware Security Module) seperti [payShield 10K HSM](https://dymarjaya.co.id/product/payshield-10000/). Hal ini dilakukan untuk mengupayakan keamanan dalam proteksi data atau aset berharga perusahaan.
Untuk informasi bagaimana mengintegrasikan aplikasi dengan HSM, dapat mengunjungi [repository](https://github.com/dymarjaya/payshield-hsm-api) berikut ini. Untuk informasi lebih lanjut dapat menghubungi [Dymar](https://www.dymarjaya.co.id). 
<br>

## Referensi Source Code
* AESKeyFinder dan RSAKeyFinder: https://citp.princeton.edu/our-work/memory/code
* AES Finder: https://github.com/mmozeiko/aes-finder
* Sample Java RSA Digital Signature, Java AES Encryption, Golang RSA Digital Signature, Golang AES Encryption: https://github.com/dymarjaya/finding-key-memory
