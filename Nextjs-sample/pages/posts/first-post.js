import Link from "next/link";
import Head from "next/head";
import Script from "next/script";
import Layout from "../../components/layout";
import Alert from "../../components/alert";
import { useEffect, useState } from "react";
import Card from "../../components/card";
export default function FirstPost() {
  const [alertType, setAlertType] = useState("success");

  useEffect(() => {
    const interval = setInterval(
      () => setAlertType((prevType) => (prevType === "success" ? "error" : "success")),
      2000
    );

    return () => clearInterval(interval);
  }, []);

  return (
    <>
      <Layout>
        <Head>
          <title>First Post</title>
        </Head>
        <Script
          src="https://connect.facebook.net/en_US/sdk.js"
          strategy="lazyOnload"
          onLoad={() => console.log("script loaded correctly, window.FB has been populated")}
        />
        <Card prompt="Can coffee make you a better developer?" type={alertType} />
        {/* <Alert type={alertType}>Welcome to the first post page</Alert> */}
      </Layout>
    </>
  );
}
