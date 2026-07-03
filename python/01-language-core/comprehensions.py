# [ EXPRESSION   for ITEM in ITERABLE   if CONDITION ]
#  what to keep      the loop            optional filter




l1=[1,2,3,4,5,6,7,8,9]

sq_l=[x*x for x in l1]
print(sq_l)


d={x:x+1 for x in range(5)}
print(d)

se={x for x in range(5)}
print(d)


sq_g=(x*x for x in range(5))
print(sq_g)

su=sum(x*x for x in range(5))
print(su)






























