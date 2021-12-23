// javascript type guard
type EitherOr = string | number;

export const additionType = (a: EitherOr, b: EitherOr): number => {
    if (typeof a === "number" && typeof b === "number"){
        return a + b;
    }
}


// "property" in object
interface SystemUser {
    userID: string;
    roles: string[];
    title: string;
}

interface AdminUser {
    passcode: string;
}

type UnknownUser = SystemUser | AdminUser;

const printEmployeeCredentials = (emp: UnknownUser) => {
    if ("userID" in emp){
        console.log(emp.userID);
    }
}

printEmployeeCredentials({userID: '1237924'} as UnknownUser);


// instanceof (class)
try { 
    throw new Error('failed to do something');
} catch (error){
    error instanceof Error ? console.log(error) : console.log('threw unknown error');
}
