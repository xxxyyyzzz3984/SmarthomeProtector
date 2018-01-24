import os
from os import listdir
from os.path import isfile, join
import angr
from cle import CLECompatibilityError
import networkx as nx
from networkx.algorithms.traversal.breadth_first_search import bfs_edges

def rough_scan(project):
    isMatch = False
    socket_symbol = project.loader.find_symbol('socket')
    bind_symbol = project.loader.find_symbol('bind')
    listen_symbol = project.loader.find_symbol('listen')
    accept_symbol = project.loader.find_symbol('accept')
    recv_symbol = project.loader.find_symbol('recv')
    read_symbol = project.loader.find_symbol('read')

    if socket_symbol is not None:
        if bind_symbol is not None:
            if listen_symbol is not None:
                if accept_symbol is not None:
                    if read_symbol is not None or recv_symbol is not None:
                        isMatch = True

    return isMatch

def CFGScan(project):
    try:
        cfg = project.analyses.CFGFast()
    except (nx.exception.NetworkXError, ) as e:
        # print e
        return False

    cfg_graph = cfg.graph
    all_nodes = cfg_graph.nodes()
    main_node = None
    for single_node in all_nodes:
        if single_node.name == 'main':
            main_node = single_node

    if main_node:
        call_list = []
        for edge in bfs_edges(cfg_graph, main_node):
            call_list.append(edge[0].name)
            call_list.append(edge[1].name)

        socket_index = call_list.index('socket')
        bind_index = call_list.index('bind')
        listen_index = call_list.index('listen')
        accept_index = call_list.index('accept')
        try:
            recv_index = call_list.index('recv')
        except ValueError:
            recv_index = 0
        try:
            read_index = call_list.index('read')
        except ValueError:
            read_index = 0

        recv_index = max(recv_index, read_index)

        if socket_index < bind_index < listen_index < accept_index < recv_index:
            return True

    else:
        return False

def keyword_detection(filepath, keyword_path):
    keyword_list = []
    keyword_f = open(keyword_path, 'r')
    for line in keyword_f:
        keyword_list.append(line.strip())

    with open(filepath, mode='rb') as file:  # b is important -> binary
        fileContent = file.read()
        for keyword in keyword_list:
            # consider both little endian and big endian
            if keyword in fileContent or keyword[::-1] in fileContent:
                print 'Keyword ' + keyword + ' Found in %s!' % filepath
                return keyword

    return None

def scan(firmware_root_dir):
    print 'Analyzing firmware ' + firmware_root_dir
    os.walk(firmware_root_dir)
    all_dirs = [x[0] for x in os.walk(firmware_root_dir)]
    beststate_roughscan_res = False
    beststate_no_sensitive = False
    beststate_CFG_Res = False

    for dir in all_dirs:
        files = [f for f in listdir(dir) if isfile(join(dir, f))]
        for single_file in files:

            path = dir + '/' + single_file
            try:
                binary_proj = angr.Project(path)
                if '.so' in single_file:
                    continue

                roughscan_res = rough_scan(binary_proj)
                if roughscan_res:
                    print 'analyzing...'
                    print 'Found a rough match:'
                    print path

                    keyword = keyword_detection(path, './config/sensitive_words')

                    if keyword is None:
                        no_sensitive = True
                    else:
                        no_sensitive = False

                    # CFG_Res = CFGScan(binary_proj)
                    # if CFG_Res:
                    #     print 'Found a CFG match'
                    CFG_Res = roughscan_res

                    if no_sensitive and CFG_Res:
                        beststate_roughscan_res = True
                        beststate_no_sensitive = True
                        beststate_CFG_Res = True

                    elif not beststate_no_sensitive and no_sensitive:
                        beststate_roughscan_res = True
                        beststate_no_sensitive = True
                        beststate_CFG_Res = False

                    elif not beststate_no_sensitive and \
                            not beststate_CFG_Res and beststate_CFG_Res:
                        beststate_roughscan_res = True
                        beststate_no_sensitive = False
                        beststate_CFG_Res = True

            except CLECompatibilityError:
                pass

    return beststate_roughscan_res, beststate_no_sensitive, beststate_CFG_Res


# print "The final result is " + str(scan('/home/xyh3984/Downloads/smartplug/'))
