local createOrderList = KEYS[1]
local takerOrderDetailKey = KEYS[2]
local makerOrderDetailKey = KEYS[3]

local takerOrderId = ARGV[1]
local lotsGap = ARGV[2]


local result = {}
local result_1 = redis.call('LREM', createOrderList, 0, takerOrderId)
local result_2 = redis.call('DEL', takerOrderDetailKey)
local result_3 = redis.call('HSET', makerOrderDetailKey, "\"lots\"", lotsGap)


table.insert(result,result_1)
table.insert(result,result_2)
table.insert(result,result_3)


return result