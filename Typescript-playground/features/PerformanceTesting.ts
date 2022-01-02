const testNumArray = [];

for (let i = 0; i < 1000; i++) {
    testNumArray.push(i);
}

// ~ 0.05 - 0.15 ms for 1000 operations
// ~ 4 MB memory
export const mapTest = (): number[] => {
    const start = performance.now();
    const output = testNumArray.map((num) => num * 2);

    const end = performance.now();
    console.log(`array.map execution time is ${end - start}`);
    const { heapUsed } = process.memoryUsage();
    console.log(`array.map memory usage is ${heapUsed / 1024 / 1024} MB`);
    return output;
};

// 0.15 - 0.4 ms for 1000 operations
// ~ 4 MB memory
export const forLoopTest = (): number[] => {
    const start = performance.now();
    const output: number[] = [];

    for (let i = 0; i < testNumArray.length; i++) {
        output.push(testNumArray[i] * 2);
    }
    const end = performance.now();
    console.log(`for loop execution time is ${end - start}`);
    const { heapUsed } = process.memoryUsage();
    console.log(`for loop memory usage is ${heapUsed / 1024 / 1024} MB`);
    return output;
};

// ~0.3 - 0.5 ms for 1000 operations
// ~ 4 MB memory
export const forOfTest = (): number[] => {
    const start = performance.now();
    const output: number[] = [];

    for (const number of testNumArray) {
        output.push(number * 2);
    }

    const end = performance.now();
    console.log(`for-of loop execution time is ${end - start}`);
    const { heapUsed } = process.memoryUsage();
    console.log(`for-of loop memory usage is ${heapUsed / 1024 / 1024} MB`);
    return output;
};
