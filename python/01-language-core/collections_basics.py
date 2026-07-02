# list 

l=['divakar',25,5.7,None]

for item in l :
    print(item)

print(l[::2]) # this will skip one start from first till end
l.append("insert last")
l.insert(0,"insert first")   
print(l)
print(l[-1])  # from back ward , index start from -1 
print(len(l))
l.pop() # delete last 
print(l)
del l[0] #delete first element 
print(l)

#  to find/search the index
print(l.index(25)) 




# tuples 

t=tuple(l)

for item in t :
    print(item)

print(t[::2]) # this will skip one start from first till end   
print(t[-1])  # from back ward , index start from -1 
print(len(t))






# dictionary 

d={}

d["name"]="Divakar"
d['age']=25

print(d["age"])

for k,v in d.items():
    print(k,v)







# set

s=set()
s.add("divakar")
s.add("divakar")
s.add("divakar")
s.add(25)
s.add(25)
s.add(25)
s.add(25)

print(s)

