(() => {
    console.log('starting app.ts');

    function nextFit(weight: number[], binCapacity: number) {
        let bins = [];

        for (let i = 0; i < weight.length; i++) {
            const currentWeight = weight[i];
            const idealFitValue = binCapacity - currentWeight;
            const idealFitIndex = weight.findIndex((item) => item === idealFitValue);
            if (idealFitIndex > 0 && idealFitIndex > i) {
                bins.push([currentWeight, idealFitValue]);
                weight.splice(idealFitIndex, 1);
            } else {
                const availableBin = bins.find(
                    (bin: number[]) =>
                        !bin ||
                        bin.reduce(
                            (prevValue: number, nextValue: number) => prevValue + nextValue,
                            0
                        ) < binCapacity
                );
                if (availableBin) {
                    availableBin.push(currentWeight);
                } else {
                    const newBin = [];
                    newBin.push(currentWeight);
                    bins.push(newBin);
                }
            }
        }

        return bins.length;
    }

    let weight = [2, 5, 4, 7, 1, 3, 8];
    let c = 10;
    let n = weight.length;
    const output = nextFit(weight, c);
    console.log(`output is ${output}`);

    console.log('closing app.ts');
})();
