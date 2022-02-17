import { Editor } from "@tinymce/tinymce-react";
import React, { useEffect, useRef, useState } from "react";
import { connect } from "react-redux";

import axios from "../../axios";
import Button from "../../components/UI/Button/Button";
import Input from "../../components/UI/Input/Input";
import Modal from "../../components/UI/Modal/Modal";
import withErrorHandler from "../../containers/HOC/withErrorHandler";
import classes from "./NewPost.module.css";

const NewPost = props => {
  const { teamName, teamId, listingId } = props.match.params;
  const editorRef = useRef(null);
  const fileRef = useRef(null);
  const [title, setTitle] = useState("");
  const [initialContent, setInitialContent] = useState("");
  const [uploadedFiles, setUploadedFiles] = useState();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedFile, setSelectedFile] = useState({});

  // const log = () => {
  // 	if (editorRef.current) {
  // 		console.log(editorRef.current.getContent());
  // 	}
  // };

  useEffect(async () => {
    if (listingId) {
      try {
        const { data: currentListing } = await axios.get(`/getEditListing?listingId=${listingId}`);
        setTitle(currentListing.title);
        setInitialContent(currentListing.content);
        setUploadedFiles(currentListing.listingFiles);
      } catch (error) {
        console.error(error);
      }
    }
  }, [listingId]);

  const handleSubmit = event => {
    event.preventDefault();
    console.log(`Selected file - ${fileRef.current.files[0].name}`);
  };

  const onSavePostHandler = async () => {
    const URL = `/addListing`;

    let formData = new FormData();
    formData.append("title", title);
    formData.append("teamId", teamId);
    formData.append("content", editorRef.current.getContent());
    formData.append("listingId", listingId ? listingId : "");
    formData.append("date", new Date());

    const files = fileRef.current.files;
    for (let file of files) {
      formData.append("file", file);
    }

    const config = {
      method: "post",
      url: URL,
      data: formData,
      contentType: false,
      processData: false,
      headers: {
        Accept: "application/json",
        Enctype: "multipart/form-data",
        Authorization: props.idToken,
      },
    };
    try {
      await axios(config);
      props.history.push(`/teams/${teamName}`);
    } catch (error) {
      console.error(error);
    }
  };

  const onFileClickedHandler = file => {
    setIsModalOpen(true);

    setSelectedFile({
      fileId: file.listingFileId,
      title: file.title,
    });
  };

  const deleteFileHandler = async () => {
    console.log("I'm deleting a file!");
    setIsModalOpen(false);
    const URL = `deleteListingFile?listingFileId=${selectedFile.fileId}`;
    const headers = {
      headers: {
        Authorization: props.idToken,
      },
    };
    try {
      await axios.post(URL, {}, headers);
      const currentFiles = uploadedFiles.filter(file => file.listingFileId !== selectedFile.fileId);
      setUploadedFiles(currentFiles);
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <>
      <div className={classes.containerDiv}>
        <h2>{teamName}</h2>
        <Input
          elementType="input"
          elementConfig={{ placeholder: "The Title of Your Post" }}
          changed={event => setTitle(event.target.value)}
          value={title}
        />
        <div>
          <Editor
            apiKey="uhqthmuk7wguamcalt0vbb5ldts8y1on2pg2ugqj5p89cizl"
            onInit={(evt, editor) => (editorRef.current = editor)}
            init={{
              height: "400",
              mobile: {
                menubar: true,
                plugins: ["lists", "autolink"],
                toolbar: [],
              },
              menubar: false,
              plugins: [
                "advlist autolink lists link image charmap print preview anchor",
                "searchreplace visualblocks code fullscreen",
                "insertdatetime media table paste code help wordcount",
              ],
              toolbar:
                "undo redo | formatselect | " +
                "bold italic | link media forecolor backcolor |" +
                "alignleft aligncenter alignright alignjustify |" +
                "bullist numlist outdent indent removeformat help",
              content_style: "body { font-family:Helvetica,Arial,sans-serif; font-size:14px }",
            }}
            initialValue={initialContent}
          />
        </div>
        <div style={{ display: "flex", flexDirection: "column" }}>
          {uploadedFiles &&
            uploadedFiles.map(file => (
              <p
                style={{
                  margin: "8px 0 0 0",
                  display: "flex",
                  alignSelf: "flex-end",
                }}
              >
                <a href={file.filePath} target="_blank" rel="noreferrer">
                  {file.title}
                </a>
                <span onClick={() => onFileClickedHandler(file)} style={{ cursor: "pointer" }}>
                  x
                </span>
              </p>
            ))}
        </div>
        <input type="file" multiple onChange={handleSubmit} ref={fileRef} style={{ marginTop: "20px" }} />
        <div>
          <Button clicked={() => onSavePostHandler()}>Submit</Button>
        </div>
      </div>
      <Modal show={isModalOpen} modalClosed={() => setIsModalOpen(false)}>
        <div style={{ display: "flex", flexDirection: "column" }}>
          Are you sure that you want to delete the file,
          <br />
          {selectedFile.title}?
          <div
            style={{
              display: "flex",
              flexDirection: "row",
              justifyContent: "center",
            }}
          >
            <Button
              clicked={deleteFileHandler}
              config={{
                width: "50px",
                backgroundColor: "#DC5F43",
                margin: "20px 25px 5px 25px",
              }}
            >
              Yes
            </Button>
            <Button clicked={() => setIsModalOpen(false)} config={{ width: "50px", margin: "20px 25px 5px 25px" }}>
              No
            </Button>
          </div>
        </div>
      </Modal>
    </>
  );
};

const mapStateToProps = state => ({
  idToken: state.idToken,
});

export default connect(mapStateToProps, null)(withErrorHandler(NewPost, axios));
