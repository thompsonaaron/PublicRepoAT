let user;
user = {
    UserID: '1lhasflhlasfhuha'
};
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
//# sourceMappingURL=Types.js.map