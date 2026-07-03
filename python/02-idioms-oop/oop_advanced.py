# A base class Animal with __init__(self, name) and a speak() returning "...".
# A subclass Dog(Animal) that overrides speak(), and a Cat(Animal) that adds a field via super().__init__(name).
# A class Circle with a @property area (computed) and a radius setter that rejects negatives.
# A @dataclass Task with title: str and done: bool = False; create two, print them, and test ==.



from dataclasses import dataclass


class Animal:
    def __init__(self,name:str=""):
        self.name=name
    
    def speak(self):
        print("...")

class Dog(Animal):
    def speak(self):
        print("Bark")

class Cat(Animal):
    def __init__(self, name = "",indore=True):
        super().__init__(name)
        self.indore=indore
    

dog=Dog("pet dog")
cat=Cat("tom",True)


dog.speak()
cat.speak()




print("---------------------------------")


class Circle:
    def __init__(self,radius):
        self.__radius=radius

    @property                   # Get
    def radius(self):
        return self.__radius

    @radius.setter              # Set
    def radius(self,r):
        if r<=0:
            raise ValueError(" Radius should be >0")
        else:
            self.__radius=r 

    @property
    def area(self):
        return 3.14159 * self.__radius ** 2
    

c=Circle(10)
print(c.radius)
print(c.area)
c.radius=5
print(c.radius)
print(c.area)






print("-------------------------------------")

@dataclass
class DataClassTask:
    title:str
    int_value:int=25
    another_int_value:int=30
    done:bool=False


t = DataClassTask("Task1",31,26)
print(t)                      # DataClassTask(title='Buy milk', done=False)   ← __repr__ auto
print(DataClassTask("x") == DataClassTask("x"))   
    



















