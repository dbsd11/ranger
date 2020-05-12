#!/bin/sh
    echo "xpack.security.enabled: false" >> ${es_home}/config/elasticsearch.yml
    echo "-Djava.security.policy=${es_home}/plugins/ranger-elasticsearch-plugin/plugin-security.policy" >> ${es_home}/config/jvm.options
    echo -e "logger.org_apache_hadoop_security_UserGroupInformation.name=org.apache.hadoop.security.UserGroupInformation
logger.org_apache_hadoop_security_UserGroupInformation.level=off" >> ${es_home}/config/log4j2.properties