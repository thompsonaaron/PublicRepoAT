console.log('hey hey!!!!');
console.log("I'm testy as fuck!");
// javascript type guard
const addition = (a, b) => {
    if (typeof a === "number" && typeof b === "number") {
        return a + b;
    }
};
const printEmployeeCredentials = (emp) => {
    if ("userID" in emp) {
        console.log(emp.userID);
    }
};
printEmployeeCredentials({ userID: '1237924' });
let user;
user = {
    UserID: '1lhasflhlasfhuha'
};
//user.UserID = 'klj;hdo;ghaoisf'; // typescript error
try {
    throw new Error('failed to do something');
}
catch (error) {
    error instanceof Error ? console.log(error) : console.log('threw unknown error');
}
const moveAnimal = (animal) => {
    switch (animal.type) {
        case 'bird':
            console.log(`the bird is running ${animal.flyingSpeed} mph`);
            break;
        case 'horse':
            console.log(`the bird is running ${animal.runningSpeed} mph`);
            break;
    }
    if ("runningSpeed" in animal) {
        console.log(animal.runningSpeed);
    }
};
moveAnimal({ type: 'bird', flyingSpeed: 20 });
const formatDropdownItems = (data, options) => {
    const { key, value, text } = options;
    return data.map(arrayObject => ({ key: arrayObject[key], value: arrayObject[value], text: arrayObject[value] }));
};
const testArray = [{ money: 150, name: 'Aaron' }, { money: 75, name: 'Rachel' }];
const output = formatDropdownItems(testArray, { key: 'name', value: 'money', text: 'name' });
console.log(JSON.stringify(output));
//# sourceMappingURL=app.js.map