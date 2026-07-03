def fun(a):
    print(a)


fun(10)

def typedFun(a:int)->int:
    return a+10

print(typedFun(10))




def keyWordFunction(name:str,age:int):
    print(f"Name:{name}, age:{age}")

    
keyWordFunction(age=25,name="Diva")


def multipleReturns(*a):
    return max(a),min(a)

l=[1,2,3,4,5,6,7,8,9]
max,min=multipleReturns(*l)
print(max,min)


def returnDict(**keyValue):
    return keyValue


print(returnDict(name="diva",age=25))




a,b,c=(1,2,3)

print(a)
print(b)
print(c)

