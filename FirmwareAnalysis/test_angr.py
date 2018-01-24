import angr
import networkx as nx

from networkx.algorithms.traversal.breadth_first_search import bfs_edges

import warnings

proj = angr.Project('/home/xyh3984/Downloads'
                    '/smartplug'
                    '/squashfs-root/usr/sbin/login')
import pylab

# proj = angr.Project('/home/xyh3984/Desktop/simple', load_options={'auto_load_libs': False})

# print hex(proj.entry)
# obj = proj.loader.main_object
# test_backend = Backend('/home/xyh3984/Desktop/simple')
# test = idabin.IDABin(test_backend)
# test.get_strings()
# addr = obj.plt['printf']
# print hex(addr)
# print proj.loader.find_symbol('printf')

print proj.loader

s = proj.factory.entry_state()
# bvv = s.memory.load(0x40fb20, 10, endness=angr.archinfo.Endness.BE)
# print bvv
# print hex(s.solver.eval(bvv))
# state.solver.eval(bv)
# print s.mem[0x40fb20].deref.string.resolved

# cfg = proj.analyses.CFGFast()
# G = cfg.graph
# all_nodes = G.nodes()
# main_node = None
# for single_node in all_nodes:
#     if single_node.name == 'main':
#         main_node = single_node

# print main_node
# print G.successors(main_node)
# call_list = []
# for edge in bfs_edges(G, main_node):
#     call_list.append(edge[0].name)
#     call_list.append(edge[1].name)
#
# print call_list
# print call_list.index('func1')
# print call_list.index('func2')
# print call_list.index('func3')
# print call_list.index('func5')

# nx.draw(G, with_labels = True)
# print G.successors()
# pylab.show()
# print G.edges()

# print proj.arch
# print hex(proj.entry)
# # print proj.loader
# # print proj.loader.min_addr, proj.loader.max_addr
# # print proj.loader.main_object.execstack
# # print proj.loader.main_object.pic
#
# block = proj.factory.block(proj.entry)
# # print block.instructions
# block.pp()
# # print block.instruction_addrs
#
# state = proj.factory.entry_state()
# print state

# simgr = proj.factory.simulation_manager(state)
# print simgr.active
#
# cfg = proj.analyses.CFGFast()
# print cfg.graph