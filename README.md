# URL shortner

## Purpose
To have ability using short urls instead of long. E.g. you want to
share link with you friend but the url is too long and ugly. 
URL shortner will produce you with the short link and when you click this link 
you will be redirected by URL shortner.

## How it works

### Main idea
The main idea is not using user url as source for short url at all and to have
pre-generated short urls. When you have pre-generated urls all you need
just choose one and map user url and short url. Advantages of this idea over
generating short urls using some hashing function are scalability and simple logic.
You should not solve conflicts of hash-functions just choose and map. You should not think
how to scale hash-function results just pre-generate any count of databases you need.

### How pre-generation works
The logic is quit simple again. We have range of numbers for unsigned long
0 - 18446744073709551615. We can just use this sequence to generate next short url.
How generate from number url. Quit simple again lets choose our new scale of notation.
Our new scale of notation will have next symbols 'A'-'z' of ASCII table. So when we want to generate
next url we just choose next number and convert it to our new scale of notation.
E.g. 0 = "A", 1 = "B", 2 = "C", 18446744073709551615 = "kp_LafePg]X".
So in the beginning we just fill our database with this sequence of number.
When we want map new url we just get one of this number map it with url then
convert this number in new scale of notation and return to user. When use this short url
we convert it again to decimal notation and this number will become our ID. We got url by this ID and redirect user.
So simple.

### Scalability
We can divide our sequence to any count op parts. 
Every part of this sequence can be placed in different database and can be managed by different nodes.
So here we have good scalability. You can scale horizontally on as many servers and nodes as you want.
But to have such scalability we need tricky load balancer. 
This balancer should know which part of sequence managed by every server. Balancer can convert short url to ID then consider ID and choose right server.

### Performance
There is performance test inside.
I can declare that on 4-core Intel i5 machine and PostgreSQL on the same machine we have about 2800 new urls per second.
How much is it? Let's take just 10% of maximum and even less e.g. 200 new urls/per second.
Per month, we will have 200 * 30 * 24 * 3600 ~ 500M new urls per month. And this number only 10% of maximum we can have.
So we can say that every node can manage at least 500M new urls per month.
