import React, { useEffect, useState } from "react";

import AlertBanner from "../../common/AlertBanner";
import Row from "react-bootstrap/Row";
import ScoopOption from "./ScoopOption";
import ToppingOption from "./ToppingOption";
import axios from "axios";

const Options = ({ optionType }) => {
	const [items, setItems] = useState([]);
	const [error, setError] = useState(false);

	useEffect(() => {
		axios
			.get(`http://localhost:3030/${optionType}`)
			.then((response) => setItems(response.data))
			.catch((error) => setError(error));
	}, [optionType]);

	const ItemComponent = optionType === "scoops" ? ScoopOption : ToppingOption;

	return (
		<Row>
			{error && <AlertBanner />}
			{items.map((item) => (
				<ItemComponent
					key={item.name}
					name={item.name}
					imagePath={item.imagePath}
				/>
			))}
		</Row>
	);
};

export default Options;
