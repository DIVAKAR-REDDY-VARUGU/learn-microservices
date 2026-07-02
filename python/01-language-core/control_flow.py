a=10

if a > 5:
    print("a is greater than 5")
elif a == 5:
    print("a is equal to 5")
else :
    print("a is less than 5")

l=[]

if not l:
    print("list is empty")


l1=[1,2,3,4,5]
l2=["a","b","c",'d']

for i1,i2 in zip(l1,l2):
    print(i1,i2)

print("--------------------------------")


for i,j in enumerate(l2):
    print(i+1,j)










