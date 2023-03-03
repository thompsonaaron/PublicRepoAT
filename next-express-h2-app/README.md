# Notes

Although the server does run, there is an issue with running this locally as Chrome will show
a connection refused error. You can either test with postman and disable a warning about
the site using a self-signed certificate or you can genearte a root certificate; for mac, see this:
https://www.eduhk.hk/ocio/content/faq-how-add-root-certificate-mac-os-x

#Takeaways
Although http/2 (H2) offers upgraded performance, creating a custom nextJS server with H2
is unnecessary unless you are hosting the site yourself. Vercel, if hosted by them, sends
all traffic between a client and proxy as HTTP2, however the communication with the proxy (e.g. NGINX)
and the server is then made via HTTP1. Running a custom server on HTTP2 may actually degrade the
performance. TL/DR: A custom HTTP2 server is pointless for a Next JS server. See here:
https://github.com/vercel/next.js/discussions/10842

# Original Source

https://itnext.io/using-http-2-with-next-js-express-917791ca249b
https://github.com/typedef42/nextjs-express-http2-example

# OpenSSL

You can install an openSSL binary, unpack, etc. or just use a Git Bash shell, which comes pre-installed
with OpenSSL and type this to create a local SSL certificate:

openssl req -x509 -newkey rsa:2048 -nodes -sha256 -keyout privateKey.key -out certificate.crt
