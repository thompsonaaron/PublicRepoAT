console.log('crazy spacing here!');
const formatDropdownItems = (data, options) => {
    const { key, value, text } = options;
    return data.map((arrayObject) => ({
        key: arrayObject[key],
        value: arrayObject[value],
        text: arrayObject[value]
    }));
};
const testArray = [
    { money: 150, name: 'Aaron' },
    { money: 75, name: 'Rachel' }
];
const output = formatDropdownItems(testArray, {
    key: 'name',
    value: 'money',
    text: 'name'
});
console.log(JSON.stringify(output));
//# sourceMappingURL=Generics.js.map