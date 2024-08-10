function invokeHttpAsync(httpRequest, callback, errorCallback) {
	const url = new URL(httpRequest.baseUrl);
	const parameters = httpRequest.parameters;
	for (const key in parameters) {
		url.searchParams.append(key, parameters[key]);
	}
	const headers = new Headers();
	headers.append('Content-Type', 'application/json');
	
	const connectTimeoutMs = 5000;
	const readTimeoutMs = 5000;
	const requestMethod = httpRequest.requestMethod;
	const options = {
		method: requestMethod,
		headers,
		timeout: connectTimeoutMs
	};
	
	const body = httpRequest.body;
	if (['POST', 'PUT'].includes(requestMethod) && body) {
		if (typeof body === 'string' || body instanceof String) {
			options.body = body;
		} else {
			options.body = JSON.stringify(body);
		}
	}
	
	Promise.resolve(new Promise((resolve, reject) => {
		const connectTimeout = setTimeout(() => {
			reject(new Error('Connection timeout'));
		}, connectTimeoutMs);

		fetch(url.toString(), options).then((response) => {
			clearTimeout(connectTimeout);
			if (readTimeoutMs) {
				const readTimeout = setTimeout(() => {
					reject(new Error('Read timeout'));
				}, readTimeoutMs);
				if (response.status !== 200 && response.status !== 204) {
					clearTimeout(readTimeout);
					reject(response.status);
				} else if (response.headers.get('Content-Length') > 0) {
					response.json()
						.then((responseData) => {
							clearTimeout(readTimeout);
							resolve(responseData);
						})
						.catch((error) => {
							clearTimeout(readTimeout);
							reject(error);
						});
				} else {
					clearTimeout(readTimeout);
					resolve('');
				}
			} else if (response.status !== 200) {
				reject(response.status);
			} else if (response.headers.get('Content-Length') > 0) {
				resolve(response.json());
			} else {
				resolve('');
			}
		})
		.catch((error) => {
			clearTimeout(connectTimeout); // Clear the connect timeout
			reject(error);
		});
	}))
	.then(callback)
	.catch(errorCallback);
}
