import React, { Component } from "react";

import Modal from "../../components/UI/Modal/Modal";

const withErrorHandler = (WrappedComponent, axios) =>
  class extends Component {
    componentWillMount() {
      this.respInterceptor = axios.interceptors.response.use(
        res => res,
        error => {
          if (error.response && error.response.data) {
            if (error.response.data.message) {
              this.setState({ error: error.response.data.message });
            } else if (error.response.data.errMsg) {
              this.setState(error.response.data.errMsg);
            } else if (error.response.data.error) {
              this.setState(error.response.data.error);
            } else {
              this.setState({ error: error.response.data });
            }
          } else {
            this.setState({ error: error.message });
          }
          return Promise.reject(error);
        }
      );
    }

    componentWillUnmount() {
      axios.interceptors.request.eject(this.reqInterceptor);
      axios.interceptors.response.eject(this.respInterceptor);
    }

    state = {
      error: null,
    };

    errorConfirmedHandler = () => {
      this.setState({ error: null });
    };

    render() {
      return (
        <>
          <Modal show={this.state.error} modalClosed={this.errorConfirmedHandler}>
            {this.state.error ? this.state.error : null}
          </Modal>
          <WrappedComponent {...this.props} />
        </>
      );
    }
  };

export default withErrorHandler;
