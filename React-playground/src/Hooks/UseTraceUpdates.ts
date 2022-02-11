import { useEffect, useRef } from "react";

interface UniversalObject {
	[key: string]: any;
}

const useTraceUpdates = (props: UniversalObject) => {
	const prev = useRef(props);

	useEffect(() => {
		const changedProps = Object.entries(props).reduce((prevValue, [k, v]) => {
			if (prev.current[k] !== v) {
				prevValue[k] = [prev.current[k], v];
			}
			return prevValue;
		}, {} as UniversalObject);

		if (Object.keys(changedProps).length) {
			console.log("Changed props:", changedProps);
		}
		prev.current = props;
	});
};

export default useTraceUpdates;
