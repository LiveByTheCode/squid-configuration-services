sed -i 's/^http_access deny all$/#http_access deny all/g' /etc/squid/squid.conf
sudo /usr/bin/systemctl reload squid.service