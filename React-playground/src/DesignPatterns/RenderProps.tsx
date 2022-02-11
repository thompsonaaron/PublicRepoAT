import React, { Fragment } from "react";

import WithColor from "./HOC/WithColor";

//The term “render prop” refers to a technique for sharing code between
// React components using a prop whose value is a function.
const Temperature = (props: any) => {
	return <>{props.children(20)}</>;
};

const saySomethingAboutTheWeather = (temp: number) => {
	return temp >= 30 ? "My brain is melting!" : "My brain is not melting";
};

interface ShowTempProps {
	temp: number;
	color?: string;
}

const ShowTemperatureInCelsius = (props: ShowTempProps) => {
	const { temp, color } = props;
	return (
		<div>
			<h2 style={{ color: `${color || "red"}` }}>Temp is {temp} ºC</h2>
			{saySomethingAboutTheWeather(temp)}
		</div>
	);
};

const ShowTemperatureInFahrenheit = (props: ShowTempProps) => {
	const { temp } = props;
	const tempInF = temp * (9 / 5) + 32;
	return (
		<div>
			<h2>Temp is {tempInF} ºF</h2>
			{saySomethingAboutTheWeather(tempInF)}
		</div>
	);
};

interface AppProps {
	color: string;
}

const RenderPropsApp: React.FC<AppProps> = (props: AppProps) => {
	const { color } = props;
	return (
		<div className="text-center my-3">
			<Temperature>
				{(temp: number) => (
					<Fragment>
						<ShowTemperatureInCelsius temp={temp} color={color} />
						<ShowTemperatureInFahrenheit temp={temp} />
					</Fragment>
				)}
			</Temperature>
		</div>
	);
};

export default WithColor(RenderPropsApp);
