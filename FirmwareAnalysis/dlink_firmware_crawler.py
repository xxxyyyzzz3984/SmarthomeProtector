import urllib
import urllib2

def recursive_handle_ftplinks(ftp_link):
    html_f = urllib2.urlopen(ftp_link)
    html_content = html_f.read()
    link_list = html_content.split('\n')
    for i in range(len(link_list)):
        link_list[i] = link_list[i].replace('\r', '')

        if link_list[i] == '':
            link_list.remove(link_list[i])
            continue

        splited_list = link_list[i].split(' ')
        link_list[i] = splited_list[len(splited_list)-1]

        link = ftp_link + '/' + link_list[i]
        if '.' not in link_list[i]:
            try:
                recursive_handle_ftplinks(link)
            except urllib2.URLError:
                print 'error handling link: ' + link

        # dealing with firmware and pdf
        else:
            if 'pdf' in link or 'PDF' in link:
                continue

            print 'Downloading ' + link
            urllib.urlretrieve(link, link_list[i])
            urllib.urlcleanup()


dlink_root_url1 = 'ftp://ftp2.dlink.com/PRODUCTS'
dlink_root_url2 = 'ftp://ftp2.dlink.com/BETA_FIRMWARE'

link_lastpart_list = recursive_handle_ftplinks(dlink_root_url1)
# for link_lastpart in copy.copy(link_lastpart_list):
#     if '.' not in link_lastpart:
