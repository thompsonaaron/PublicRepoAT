import Head from "next/head";
import Layout, { siteTitle } from "../components/layout";
import utilStyles from "../styles/utils.module.css";
import Link from "next/link";
import { Button } from "@material-tailwind/react";

export default function Home() {
  return (
    <Layout home>
      <Head>
        <title>{siteTitle}</title>
      </Head>
      <section className={utilStyles.headingMd}>
        <p className="my-4">
          I'm Aaron, a full-stack java/javascript developer and running enthusiast with a love for
          writing eloquent code that solves real-world problems.
        </p>
        <p className="my-4">
          This sample page uses Tailwind CSS and material-tailwind, a material UI
        </p>
        <p className="text-center">
          <Link href="/posts/first-post">
            <Button>See first post</Button>
          </Link>
        </p>
        <p className="my-4">
          (This sample site was built as part of Vercel's{" "}
          <a href="https://nextjs.org/learn">Next.js tutorial</a>.)
        </p>
      </section>
    </Layout>
  );
}
