
with open('/home/xyh3984/Downloads'
        '/smartplug'
        '/squashfs-root/usr/sbin/login', mode='rb') as file: # b is important -> binary
    fileContent = file.read()

    print fileContent