import React from "react";

import classes from "./Paginator.module.css";

const Paginator = props => {
  const { baseURL, logger, pageParam, pageParamValue, username, setPage } = props;

  const paginator = [];
  for (let i = 0; i < 8; i++) {
    paginator.push(
      <li
        key={`${baseURL}?${pageParam}=${i}`}
        className={i == pageParamValue ? classes.paginatorItem + " " + classes.active : classes.paginatorItem}
        onClick={() => setPage(logger || username, i)}
      >
        {i + 1}
      </li>
    );
  }
  return (
    <div>
      <ul className={classes.paginatorList}>{paginator}</ul>
    </div>
  );
};

export default Paginator;
