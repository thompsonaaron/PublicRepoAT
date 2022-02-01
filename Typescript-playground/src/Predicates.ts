enum UserRole {
    Administrator = 1,
    Editor = 2,
    Subscriber = 3,
    Writer = 4
}

interface User {
    username: string;
    age: number;
    role: UserRole;
}

const users = [
    { username: 'John', age: 25, role: UserRole.Administrator },
    { username: 'Jane', age: 7, role: UserRole.Subscriber },
    { username: 'Liza', age: 18, role: UserRole.Writer },
    { username: 'Jim', age: 16, role: UserRole.Editor },
    { username: 'Bill', age: 32, role: UserRole.Editor }
];

type PredicateFn = (value: any, index?: number) => boolean;
type ProjectionFn = (value: any, index?: number) => any;

const or = (...predicates: PredicateFn[]): PredicateFn => {
    return (value, index) => predicates.some((predicate) => predicate(value));
};

const and = (...predicates: PredicateFn[]): PredicateFn => {
    return (value, index) => predicates.every((predicate) => predicate(value));
};

const not = (...predicates: PredicateFn[]): PredicateFn => {
    return (value, index) => predicates.every((predicate) => !predicate(value));
};

// const isWriter = (user: User) => user.role === UserRole.Writer;
//const isEditor = (user: User) => user.role === UserRole.Editor;
//const isGreaterThan17 = (user: User) => user.age > 17;
//const isGreaterThan5 = (user: User) => user.age > 5;

// const greaterThan17AndWriterOrEditor = users.filter(
//     and(isGreaterThan17, or(isWriter, isEditor))
// );

// const greaterThan5AndSubscriberOrWriter = users.filter(and(isGreaterThan5, isWriter));

const isRole = (role: UserRole) => (user: User) => user.role === role;

const isGreaterThan = (age: number) => (user: User) => user.age > age;

const isWriter = isRole(UserRole.Writer);
const isEditor = isRole(UserRole.Editor);

const greaterThan17AndWriterOrEditor = users.filter(and(isGreaterThan(17), or(isWriter, isEditor)));

const greaterThan5AndSubscriberOrWriter = users.filter(and(isGreaterThan(5), isWriter));
const greaterThan5AndSubscriberOrWriterTwo = users.filter(
    (user) => user.age > 5 && user.role === 2
);
