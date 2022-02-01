import { forLoopTest, forOfTest, mapTest } from './PerformanceTesting';

describe('performance tests', () => {
    it('map beats all loops in speed and memory usage', () => {
        const [mapSpeed, mapMemory] = mapTest();
        const [forLoopSpeed, forLoopMemory] = forLoopTest();
        const [forOfLoopSpeed, forOfLoopMemory] = forOfTest();

        expect(mapSpeed).toBeLessThan(forLoopSpeed);
        expect(mapSpeed).toBeLessThan(forOfLoopSpeed);
        //expect(forLoopSpeed).toBeCloseTo(forOfLoopSpeed, 1);

        expect(mapMemory).toBeLessThan(forLoopMemory);
        expect(forLoopMemory).toBeLessThan(forOfLoopMemory);
    });
});
