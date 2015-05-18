#!/usr/bin/python

import boto
from boto.ec2.regioninfo import RegionInfo
import time
import os

# connecting to nectar cloud
region=RegionInfo(name='NeCTAR', endpoint='nova.rc.nectar.org.au') 

ec2_conn = boto.connect_ec2(aws_access_key_id="525e71b1360348709da3e1a73ee7ba13", 
aws_secret_access_key="b6ddb323b26946919317861364f0f07a", is_secure=True, region=region, 
port=8773, path='/services/Cloud', validate_certs=False)

# creat 4 instances on nectar
ec2_conn.run_instances('NeCTAR Ubuntu 14.04 (Trusty) amd64', 
	key_name='newkey', instance_type='m1.medium', placement='tasmania',
	security_groups=['Apache','ESearch','FTP','Gmail_port','HTTP','SSH','couchdb','default','dropboxreceiver'])

ec2_conn.run_instances('NeCTAR Ubuntu 14.04 (Trusty) amd64', 
	key_name='team2', instance_type='m1.medium', placement='tasmania',
	security_groups=['Apache','ESearch','FTP','Gmail_port','HTTP','SSH','couchdb','default','dropboxreceiver'])

ec2_conn.run_instances('NeCTAR Ubuntu 14.04 (Trusty) amd64', 
	key_name='team2', instance_type='m1.medium', placement='tasmania',
	security_groups=['Apache','ESearch','FTP','Gmail_port','HTTP','SSH','couchdb','default','dropboxreceiver'])

ec2_conn.run_instances('NeCTAR Ubuntu 14.04 (Trusty) amd64', 
	key_name='team2', instance_type='m1.medium', placement='tasmania',
	security_groups=['Apache','ESearch','FTP','Gmail_port','HTTP','SSH','couchdb','default','dropboxreceiver'])

# wait for 2 mins before deploy environment and software on instances
time.sleep(120)

# run the ansible playbook for auto deployment
command = 'ansible-playbook -i hosts software.yml'
os.system(command)

