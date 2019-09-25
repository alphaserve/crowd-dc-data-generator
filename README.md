Data Generator for Crowd

This is simple plugin which generates Users and Groups.

It adds web-tem into administration nav console.
Just navigate into page and choose number of entities you need to be generated.
Number of threads is optimized for approximately EC2 t2.medium instance. Feel free to edit source code to fine-tune it
to your own needs.

Please note that this plugin does NOT parallelize generation across nodes in CrowdDC instances. So generation while
several nodes are active would be a waste of computing time.
