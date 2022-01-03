// Generics
type PersonMoney = {
    name: string;
    money: number;
};

type DropdownItem = {
    [key: string]: string | number | boolean;
};

const formatDropdownItems = <T extends DropdownItem>(
    data: T[],
    options: { key: keyof T; value: keyof T; text: keyof T }
) => {
    const { key, value, text } = options;
    return data.map((arrayObject) => ({
        key: arrayObject[key],
        value: arrayObject[value],
        text: arrayObject[text]
    }));
};

const testPersonArray = [
    { money: 150, name: 'Aaron' } as PersonMoney,
    { money: 75, name: 'Rachel' } as PersonMoney
];

// eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
const output = formatDropdownItems(testPersonArray, {
    key: 'name',
    value: 'money',
    text: 'name'
});
console.log(JSON.stringify(output));
