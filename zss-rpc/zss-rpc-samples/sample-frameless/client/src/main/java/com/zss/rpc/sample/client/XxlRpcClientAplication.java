package com.zss.rpc.sample.client;


import com.zss.rpc.core.remoting.invoker.RpcInvoker;
import com.zss.rpc.core.remoting.invoker.call.CallType;
import com.zss.rpc.core.remoting.invoker.call.RpcInvokeCallback;
import com.zss.rpc.core.remoting.invoker.reference.RpcReferenceBean;
import com.zss.rpc.core.remoting.invoker.route.LoadBalance;
import com.zss.rpc.core.remoting.transport.impl.netty.client.NettyClient;
import com.zss.rpc.core.serialize.impl.Hessian2Serializer;
import com.zss.rpc.sample.api.DemoService;
import com.zss.rpc.sample.api.dto.UserDTO;

import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2018-10-21 20:48:40
 */
public class XxlRpcClientAplication {

	public static void main(String[] args) throws Exception {

		/*String serviceKey = XxlRpcProviderFactory.makeServiceKey(DemoService.class.getName(), null);
		XxlRpcInvokerFactory.getInstance().getServiceRegistry().registry(new HashSet<String>(Arrays.asList(serviceKey)), "127.0.0.1:7080");*/

		// test
		testSYNC();
		//testFUTURE();
		//testCALLBACK();
		//testONEWAY();

		TimeUnit.SECONDS.sleep(2);

		// stop client invoker factory (default by getInstance, exist inner thread, need destory)
		RpcInvoker.getInstance().stop();

	}



	/**
	 * CallType.SYNC
	 */
	public static void testSYNC() throws Exception {
		// init client
		RpcReferenceBean referenceBean = new RpcReferenceBean();
		referenceBean.setClient(NettyClient.class);
		referenceBean.setSerializer(Hessian2Serializer.class);
		referenceBean.setCallType(CallType.SYNC);
		referenceBean.setLoadBalance(LoadBalance.ROUND);
		referenceBean.setIface(DemoService.class);
		referenceBean.setVersion(null);
		referenceBean.setTimeout(5000);
		referenceBean.setAddress("127.0.0.1:7080");
		referenceBean.setAccessToken(null);
		referenceBean.setRpcInvokeCallback(null);
		referenceBean.setRpcInvoker(null);

		DemoService demoService = (DemoService) referenceBean.getProxyObject();

		// test
        UserDTO userDTO = demoService.sayHello("[SYNC]jack");
		System.out.println(userDTO);


		// test mult
		/*int count = 100;
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			UserDTO userDTO2 = demoService.sayHi("[SYNC]jack"+i );
			System.out.println(i + "##" + userDTO2.toString());
		}
		long end = System.currentTimeMillis();
    	System.out.println("run count:"+ count +", cost:" + (end - start));*/

	}


	/**
	 * CallType.FUTURE
	 */
	public static void testFUTURE() throws Exception {
		// client test
		RpcReferenceBean referenceBean = new RpcReferenceBean();
		referenceBean.setClient(NettyClient.class);
		referenceBean.setSerializer(Hessian2Serializer.class);
		referenceBean.setCallType(CallType.FUTURE);
		referenceBean.setLoadBalance(LoadBalance.ROUND);
		referenceBean.setIface(DemoService.class);
		referenceBean.setVersion(null);
		referenceBean.setTimeout(500);
		referenceBean.setAddress("127.0.0.1:7080");
		referenceBean.setAccessToken(null);
		referenceBean.setRpcInvokeCallback(null);
		referenceBean.setRpcInvoker(null);

		DemoService demoService = (DemoService) referenceBean.getProxyObject();

		// test
		demoService.sayHello("[FUTURE]jack" );
        //Future<UserDTO> userDTOFuture = RpcInvokeFuture.getFuture(UserDTO.class);
		//UserDTO userDTO = userDTOFuture.get();

		//System.out.println(userDTO.toString());
	}


	/**
	 * CallType.CALLBACK
	 */
	public static void testCALLBACK() throws Exception {
		// client test
		RpcReferenceBean referenceBean = new RpcReferenceBean();
		referenceBean.setClient(NettyClient.class);
		referenceBean.setSerializer(Hessian2Serializer.class);
		referenceBean.setCallType(CallType.CALLBACK);
		referenceBean.setLoadBalance(LoadBalance.ROUND);
		referenceBean.setIface(DemoService.class);
		referenceBean.setVersion(null);
		referenceBean.setTimeout(500);
		referenceBean.setAddress("127.0.0.1:7080");
		referenceBean.setAccessToken(null);
		referenceBean.setRpcInvokeCallback(null);
		referenceBean.setRpcInvoker(null);

		DemoService demoService = (DemoService) referenceBean.getProxyObject();


        // test
        RpcInvokeCallback.setInvokeCallback(new RpcInvokeCallback<UserDTO>() {
            @Override
            public void onSuccess(UserDTO result) {
                System.out.println(result);
            }

            @Override
            public void onFailure(Throwable exception) {
                exception.printStackTrace();
            }
        });

        demoService.sayHello("[CALLBACK]jack");
	}


	/**
	 * CallType.ONEWAY
	 */
	public static void testONEWAY() throws Exception {
		// client test
		RpcReferenceBean referenceBean = new RpcReferenceBean();
		referenceBean.setClient(NettyClient.class);
		referenceBean.setSerializer(Hessian2Serializer.class);
		referenceBean.setCallType(CallType.ONEWAY);
		referenceBean.setLoadBalance(LoadBalance.ROUND);
		referenceBean.setIface(DemoService.class);
		referenceBean.setVersion(null);
		referenceBean.setTimeout(500);
		referenceBean.setAddress("127.0.0.1:7080");
		referenceBean.setAccessToken(null);
		referenceBean.setRpcInvokeCallback(null);
		referenceBean.setRpcInvoker(null);

		DemoService demoService = (DemoService) referenceBean.getProxyObject();

		// test
        demoService.sayHello("[ONEWAY]jack");
	}

}
