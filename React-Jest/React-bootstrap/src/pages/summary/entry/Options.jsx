import React, { useEffect, useState } from "react";

import Row from "react-bootstrap/Row";
import ScoopOption from "./ScoopOption";
import axios from "axios";

const Options = ({ optionType }) => {
	const [items, setItems] = useState([]);

	useEffect(() => {
		axios
			.get(`http://localhost:3030/${optionType}`)
			.then((response) => setItems(response.data))
			.catch((error) => console.log(error));
	}, [optionType]);

	const ItemComponent = optionType === "scoops" ? ScoopOption : null;

	return (
		<Row>
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
