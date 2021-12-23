"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.additionType = void 0;
const additionType = (a, b) => {
    if (typeof a === "number" && typeof b === "number") {
        return a + b;
    }
};
exports.additionType = additionType;
const printEmployeeCredentials = (emp) => {
    if ("userID" in emp) {
        console.log(emp.userID);
    }
};
printEmployeeCredentials({ userID: '1237924' });
// instanceof (class)
try {
    throw new Error('failed to do something');
}
catch (error) {
    error instanceof Error ? console.log(error) : console.log('threw unknown error');
}
//# sourceMappingURL=TypeGuards.js.map