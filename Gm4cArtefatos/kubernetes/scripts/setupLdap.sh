ldapadd -x -w ibm123 -D "cn=admin,dc=gm4c,dc=com" -f /root/gm4c/Gm4cArtefatos/kubernetes/configs/ldap/richard.diff -H ldap://localhost:31389
ldappasswd -s ibm123 -w ibm123 -D "cn=admin,dc=gm4c,dc=com" -x "uid=richard,dc=gm4c,dc=com" -H ldap://localhost:31389 
