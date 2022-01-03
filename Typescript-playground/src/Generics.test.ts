import { PersonMoney, formatDropdownItems } from './Generics';

describe('Generics test suite', () => {
    it('formatDropdownItems', () => {
        expect(
            formatDropdownItems([{ money: 150, name: 'Aaron' } as PersonMoney], {
                key: 'name',
                value: 'money',
                text: 'name'
            })
        ).toEqual([{ key: 'Aaron', value: 150, text: 'Aaron' }]);
    });
});
