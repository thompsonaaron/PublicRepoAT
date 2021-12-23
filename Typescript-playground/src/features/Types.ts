// note this has severa type errors and eslint-plugin is currently disabled
// to prevent build errors

type Employee = {
    userID: string;
    name: string;
};

type Manager = {
    privileges: string[];
};

type CEO = Employee & Manager; // Intersection type

// types vs interfaces
type AddFunction = (a: number, b: number) => number;

interface AddFn {
    (a: number, b: number): number;
}

// readonly
interface User {
    readonly UserID: string;
}

let user: User = null;
user = {
    UserID: '1lhasflhlasfhuha'
};

//user.UserID = 'klj;hdo;ghaoisf'; // typescript error bc readonly property

// discriminated Union where objects have "type" property to distinguish apart
interface Bird {
    type: 'bird';
    flyingSpeed: number;
}

interface Horse {
    type: 'horse';
    runningSpeed: number;
}

type Animal = Bird | Horse; // union type

const moveAnimal = (animal: Animal) => {
    switch (animal.type) {
        case 'bird':
            console.log(`the bird is running ${animal.flyingSpeed} mph`);
            break;
        case 'horse':
            console.log(`the bird is running ${animal.runningSpeed} mph`);
            break;
    }

    if ('runningSpeed' in animal) {
        console.log(animal.runningSpeed);
    }
};

moveAnimal({ type: 'bird', flyingSpeed: 20 } as Bird);

// index types
interface Errors {
    [key: string]: string; // index
    id: string;
    0: string; // ok
    //1000: number; // error! The value of any property is constrained by the index type
}
