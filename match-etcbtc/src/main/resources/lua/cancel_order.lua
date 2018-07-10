local cancelOrderList = KEYS[1]
local createOrderList = KEYS[2]
local makerOrderDetailKey = KEYS[3]


local makerOrderId = ARGV[1]

local result = {}
local result_1 = redis.call('LREM', cancelOrderList, 0, makerOrderId)
local result_2 = redis.call('LREM', createOrderList, 0, makerOrderId)
local result_3 = redis.call('DEL', makerOrderDetailKey)

table.insert(result,result_1)
table.insert(result,result_2)
table.insert(result,result_3)

return result