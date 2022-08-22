# Rare Errors
## Invalid V8 thread access
I cannot reproduce this error!

Related: https://github.com/eclipsesource/J2V8/blob/6ce9ff594d9d052e3f4bf1cc29f4a65caeae146a/src/main/java/com/eclipsesource/v8/V8Locker.java#L44

<details>
<summary> typical test.check & server test things </summary>

```clojure
{:type :clojure.test.check.clojure-test/shrunk, :clojure.test.check.clojure-test/property #clojure.test.check.generators.Generator{:gen #function[clojure.test.check.generators/gen-fmap/fn--6126]}, :clojure.test.check.clojure-test/params [[]]}
"Elapsed time: 14811.007572 msecs"
{:shrunk {:total-nodes-visited 23, :depth 20, :pass? false, :result #error {
 :cause "Address already in use"
 :via
 [{:type java.io.IOException
   :message "Failed to bind to 0.0.0.0/0.0.0.0:3011"
   :at [org.eclipse.jetty.server.ServerConnector openAcceptChannel "ServerConnector.java" 349]}
  {:type java.net.BindException
   :message "Address already in use"
   :at [sun.nio.ch.Net bind0 "Net.java" -2]}]
 :trace
 [[sun.nio.ch.Net bind0 "Net.java" -2]
  [sun.nio.ch.Net bind "Net.java" 459]
  [sun.nio.ch.Net bind "Net.java" 448]
  [sun.nio.ch.ServerSocketChannelImpl bind "ServerSocketChannelImpl.java" 227]
  [sun.nio.ch.ServerSocketAdaptor bind "ServerSocketAdaptor.java" 80]
  [org.eclipse.jetty.server.ServerConnector openAcceptChannel "ServerConnector.java" 344]
  [org.eclipse.jetty.server.ServerConnector open "ServerConnector.java" 310]
  [org.eclipse.jetty.server.AbstractNetworkConnector doStart "AbstractNetworkConnector.java" 80]
  [org.eclipse.jetty.server.ServerConnector doStart "ServerConnector.java" 234]
  [org.eclipse.jetty.util.component.AbstractLifeCycle start "AbstractLifeCycle.java" 73]
  [org.eclipse.jetty.server.Server doStart "Server.java" 401]
  [org.eclipse.jetty.util.component.AbstractLifeCycle start "AbstractLifeCycle.java" 73]
  [ring.adapter.jetty$run_jetty invokeStatic "jetty.clj" 220]
  [ring.adapter.jetty$run_jetty invoke "jetty.clj" 158]
  [kur.blog.publisher$start_BANG_ invokeStatic "publisher.clj" 41]
  [kur.blog.publisher$start_BANG_ invoke "publisher.clj" 35]
  [clojure.core$update invokeStatic "core.clj" 6231]
  [clojure.core$update invoke "core.clj" 6223]
  [kur.blog.main$start_BANG_ invokeStatic "main.clj" 38]
  [kur.blog.main$start_BANG_ invoke "main.clj" 34]
  [kur.blog_test.spbt$model_test$fn__23619 invoke "NO_SOURCE_FILE" 162]
  [clojure.lang.AFn applyToHelper "AFn.java" 154]
  [clojure.lang.AFn applyTo "AFn.java" 144]
  [clojure.core$apply invokeStatic "core.clj" 667]
  [clojure.core$apply invoke "core.clj" 662]
  [clojure.test.check.properties$apply_gen$fn__6805$fn__6806 invoke "properties.cljc" 31]
  [clojure.test.check.properties$apply_gen$fn__6805 invoke "properties.cljc" 30]
  [clojure.test.check.rose_tree$fmap invokeStatic "rose_tree.cljc" 77]
  [clojure.test.check.rose_tree$fmap invoke "rose_tree.cljc" 73]
  [clojure.test.check.rose_tree$fmap$fn__6023 invoke "rose_tree.cljc" 77]
  [clojure.core$map$fn__5935 invoke "core.clj" 2772]
  [clojure.lang.LazySeq sval "LazySeq.java" 42]
  [clojure.lang.LazySeq seq "LazySeq.java" 51]
  [clojure.lang.RT seq "RT.java" 535]
  [clojure.core$seq__5467 invokeStatic "core.clj" 139]
  [clojure.core$seq__5467 invoke "core.clj" 139]
  [clojure.test.check$shrink_loop invokeStatic "check.cljc" 293]
  [clojure.test.check$shrink_loop invoke "check.cljc" 242]
  [clojure.test.check$failure invokeStatic "check.cljc" 314]
  [clojure.test.check$failure invoke "check.cljc" 297]
  [clojure.test.check$quick_check invokeStatic "check.cljc" 228]
  [clojure.test.check$quick_check doInvoke "check.cljc" 59]
  [clojure.lang.RestFn applyTo "RestFn.java" 142]
  [clojure.core$apply invokeStatic "core.clj" 671]
  [clojure.core$apply invoke "core.clj" 662]
  [kur.blog_test.spbt$model_test invokeStatic "NO_SOURCE_FILE" 137]
  [kur.blog_test.spbt$model_test doInvoke "NO_SOURCE_FILE" 137]
  [clojure.lang.RestFn applyTo "RestFn.java" 139]
  [clojure.core$apply invokeStatic "core.clj" 669]
  [clojure.core$apply invoke "core.clj" 662]
  [kur.blog_test.spbt$model_test invokeStatic "NO_SOURCE_FILE" 137]
  [kur.blog_test.spbt$model_test invoke "NO_SOURCE_FILE" 137]
  [kur.blog_test.spbt$eval23633 invokeStatic "NO_SOURCE_FILE" 230]
  [kur.blog_test.spbt$eval23633 invoke "NO_SOURCE_FILE" 230]
  [clojure.lang.Compiler eval "Compiler.java" 7194]
  [clojure.lang.Compiler eval "Compiler.java" 7184]
  [clojure.lang.Compiler eval "Compiler.java" 7149]
  [clojure.core$eval invokeStatic "core.clj" 3215]
  [clojure.core$eval invoke "core.clj" 3211]
  [nrepl.middleware.interruptible_eval$evaluate$fn__1245$fn__1246 invoke "interruptible_eval.clj" 87]
  [clojure.lang.AFn applyToHelper "AFn.java" 152]
  [clojure.lang.AFn applyTo "AFn.java" 144]
  [clojure.core$apply invokeStatic "core.clj" 667]
  [clojure.core$with_bindings_STAR_ invokeStatic "core.clj" 1990]
  [clojure.core$with_bindings_STAR_ doInvoke "core.clj" 1990]
  [clojure.lang.RestFn invoke "RestFn.java" 425]
  [nrepl.middleware.interruptible_eval$evaluate$fn__1245 invoke "interruptible_eval.clj" 87]
  [clojure.main$repl$read_eval_print__9206$fn__9209 invoke "main.clj" 437]
  [clojure.main$repl$read_eval_print__9206 invoke "main.clj" 437]
  [clojure.main$repl$fn__9215 invoke "main.clj" 458]
  [clojure.main$repl invokeStatic "main.clj" 458]
  [clojure.main$repl doInvoke "main.clj" 368]
  [clojure.lang.RestFn invoke "RestFn.java" 1523]
  [nrepl.middleware.interruptible_eval$evaluate invokeStatic "interruptible_eval.clj" 84]
  [nrepl.middleware.interruptible_eval$evaluate invoke "interruptible_eval.clj" 56]
  [nrepl.middleware.interruptible_eval$interruptible_eval$fn__1278$fn__1282 invoke "interruptible_eval.clj" 152]
  [clojure.lang.AFn run "AFn.java" 22]
  [nrepl.middleware.session$session_exec$main_loop__1348$fn__1352 invoke "session.clj" 218]
  [nrepl.middleware.session$session_exec$main_loop__1348 invoke "session.clj" 217]
  [clojure.lang.AFn run "AFn.java" 22]
  [java.lang.Thread run "Thread.java" 829]]}, :result-data #:clojure.test.check.properties{:error #error {
 :cause "Address already in use"
 :via
 [{:type java.io.IOException
   :message "Failed to bind to 0.0.0.0/0.0.0.0:3011"
   :at [org.eclipse.jetty.server.ServerConnector openAcceptChannel "ServerConnector.java" 349]}
  {:type java.net.BindException
   :message "Address already in use"
   :at [sun.nio.ch.Net bind0 "Net.java" -2]}]
 :trace
 [[sun.nio.ch.Net bind0 "Net.java" -2]
  [sun.nio.ch.Net bind "Net.java" 459]
  [sun.nio.ch.Net bind "Net.java" 448]
  [sun.nio.ch.ServerSocketChannelImpl bind "ServerSocketChannelImpl.java" 227]
  [sun.nio.ch.ServerSocketAdaptor bind "ServerSocketAdaptor.java" 80]
  [org.eclipse.jetty.server.ServerConnector openAcceptChannel "ServerConnector.java" 344]
  [org.eclipse.jetty.server.ServerConnector open "ServerConnector.java" 310]
  [org.eclipse.jetty.server.AbstractNetworkConnector doStart "AbstractNetworkConnector.java" 80]
  [org.eclipse.jetty.server.ServerConnector doStart "ServerConnector.java" 234]
  [org.eclipse.jetty.util.component.AbstractLifeCycle start "AbstractLifeCycle.java" 73]
  [org.eclipse.jetty.server.Server doStart "Server.java" 401]
  [org.eclipse.jetty.util.component.AbstractLifeCycle start "AbstractLifeCycle.java" 73]
  [ring.adapter.jetty$run_jetty invokeStatic "jetty.clj" 220]
  [ring.adapter.jetty$run_jetty invoke "jetty.clj" 158]
  [kur.blog.publisher$start_BANG_ invokeStatic "publisher.clj" 41]
  [kur.blog.publisher$start_BANG_ invoke "publisher.clj" 35]
  [clojure.core$update invokeStatic "core.clj" 6231]
  [clojure.core$update invoke "core.clj" 6223]
  [kur.blog.main$start_BANG_ invokeStatic "main.clj" 38]
  [kur.blog.main$start_BANG_ invoke "main.clj" 34]
  [kur.blog_test.spbt$model_test$fn__23619 invoke "NO_SOURCE_FILE" 162]
  [clojure.lang.AFn applyToHelper "AFn.java" 154]
  [clojure.lang.AFn applyTo "AFn.java" 144]
  [clojure.core$apply invokeStatic "core.clj" 667]
  [clojure.core$apply invoke "core.clj" 662]
  [clojure.test.check.properties$apply_gen$fn__6805$fn__6806 invoke "properties.cljc" 31]
  [clojure.test.check.properties$apply_gen$fn__6805 invoke "properties.cljc" 30]
  [clojure.test.check.rose_tree$fmap invokeStatic "rose_tree.cljc" 77]
  [clojure.test.check.rose_tree$fmap invoke "rose_tree.cljc" 73]
  [clojure.test.check.rose_tree$fmap$fn__6023 invoke "rose_tree.cljc" 77]
  [clojure.core$map$fn__5935 invoke "core.clj" 2772]
  [clojure.lang.LazySeq sval "LazySeq.java" 42]
  [clojure.lang.LazySeq seq "LazySeq.java" 51]
  [clojure.lang.RT seq "RT.java" 535]
  [clojure.core$seq__5467 invokeStatic "core.clj" 139]
  [clojure.core$seq__5467 invoke "core.clj" 139]
  [clojure.test.check$shrink_loop invokeStatic "check.cljc" 293]
  [clojure.test.check$shrink_loop invoke "check.cljc" 242]
  [clojure.test.check$failure invokeStatic "check.cljc" 314]
  [clojure.test.check$failure invoke "check.cljc" 297]
  [clojure.test.check$quick_check invokeStatic "check.cljc" 228]
  [clojure.test.check$quick_check doInvoke "check.cljc" 59]
  [clojure.lang.RestFn applyTo "RestFn.java" 142]
  [clojure.core$apply invokeStatic "core.clj" 671]
  [clojure.core$apply invoke "core.clj" 662]
  [kur.blog_test.spbt$model_test invokeStatic "NO_SOURCE_FILE" 137]
  [kur.blog_test.spbt$model_test doInvoke "NO_SOURCE_FILE" 137]
  [clojure.lang.RestFn applyTo "RestFn.java" 139]
  [clojure.core$apply invokeStatic "core.clj" 669]
  [clojure.core$apply invoke "core.clj" 662]
  [kur.blog_test.spbt$model_test invokeStatic "NO_SOURCE_FILE" 137]
  [kur.blog_test.spbt$model_test invoke "NO_SOURCE_FILE" 137]
  [kur.blog_test.spbt$eval23633 invokeStatic "NO_SOURCE_FILE" 230]
  [kur.blog_test.spbt$eval23633 invoke "NO_SOURCE_FILE" 230]
  [clojure.lang.Compiler eval "Compiler.java" 7194]
  [clojure.lang.Compiler eval "Compiler.java" 7184]
  [clojure.lang.Compiler eval "Compiler.java" 7149]
  [clojure.core$eval invokeStatic "core.clj" 3215]
  [clojure.core$eval invoke "core.clj" 3211]
  [nrepl.middleware.interruptible_eval$evaluate$fn__1245$fn__1246 invoke "interruptible_eval.clj" 87]
  [clojure.lang.AFn applyToHelper "AFn.java" 152]
  [clojure.lang.AFn applyTo "AFn.java" 144]
  [clojure.core$apply invokeStatic "core.clj" 667]
  [clojure.core$with_bindings_STAR_ invokeStatic "core.clj" 1990]
  [clojure.core$with_bindings_STAR_ doInvoke "core.clj" 1990]
  [clojure.lang.RestFn invoke "RestFn.java" 425]
  [nrepl.middleware.interruptible_eval$evaluate$fn__1245 invoke "interruptible_eval.clj" 87]
  [clojure.main$repl$read_eval_print__9206$fn__9209 invoke "main.clj" 437]
  [clojure.main$repl$read_eval_print__9206 invoke "main.clj" 437]
  [clojure.main$repl$fn__9215 invoke "main.clj" 458]
  [clojure.main$repl invokeStatic "main.clj" 458]
  [clojure.main$repl doInvoke "main.clj" 368]
  [clojure.lang.RestFn invoke "RestFn.java" 1523]
  [nrepl.middleware.interruptible_eval$evaluate invokeStatic "interruptible_eval.clj" 84]
  [nrepl.middleware.interruptible_eval$evaluate invoke "interruptible_eval.clj" 56]
  [nrepl.middleware.interruptible_eval$interruptible_eval$fn__1278$fn__1282 invoke "interruptible_eval.clj" 152]
  [clojure.lang.AFn run "AFn.java" 22]
  [nrepl.middleware.session$session_exec$main_loop__1348$fn__1352 invoke "session.clj" 218]
  [nrepl.middleware.session$session_exec$main_loop__1348 invoke "session.clj" 217]
  [clojure.lang.AFn run "AFn.java" 22]
  [java.lang.Thread run "Thread.java" 829]]}}, :time-shrinking-ms 352, :smallest [[]]},
```
</details>

<details>
<summary> test detalis and generated ops </summary>

```clojure
 :failed-after-ms 14453,
 :num-tests 15,
 :seed 1661126771421,
 :fail
 [[{:id "6QHF7b8v0nePd0B7001010900", :kind :delete}
   {:kind :wait}
   {:kind :n-publics}
   {:kind :n-publics}
   {:kind :n-publics}
   {:kind :read,
    :url
    "http://localhost:3011/42anI7001010859.f%EC%89%99-%EB%94%86%EC%94%B4%EB%A3%AEZ+J%EB%A4%B0%20%ED%83%90%EB%AE%88%21%EB%95%AA%EC%B5%9F.%EC%95%86-%EB%BA%BF%EC%88%8C28A%EA%BA%96%28%20"}
   {:kind :n-publics}
   {:kind :n-publics}
   {:kind :n-publics}
   {:kind :n-publics}
   {:id "t7001010900",
    :kind :create,
    :post
    {:kur.blog.post/id "t7001010900",
     :kur.blog.post/meta-str "+",
     :kur.blog.post/public? true,
     :kur.blog.post/path "test/fixture/post-md/t7001010900.+.md",
     :md-text "\r\t느쿕G쐉쾠r\r걂횮뾪]\t욁덕혿넶핐b맘\n  \n  "}}
   {:kind :wait}
   {:kind :read, :url "http://localhost:3011/o8Xgnmji"}
   {:kind :read,
    :url "http://localhost:3011/t7001010900",
    :id "t7001010900",
    :post
    {:kur.blog.post/id "t7001010900",
     :kur.blog.post/meta-str "+",
     :kur.blog.post/public? true,
     :kur.blog.post/path "test/fixture/post-md/t7001010900.+.md",
     :md-text "\r\t느쿕G쐉쾠r\r걂횮뾪]\t욁덕혿넶핐b맘\n  \n  "}}
   {:id "88ZzN4V7001010859",
    :kind :create,
    :post
    {:kur.blog.post/id "88ZzN4V7001010859",
     :kur.blog.post/public? nil,
     :kur.blog.post/path "test/fixture/post-md/88ZzN4V7001010859.md",
     :md-text "짛폫+\n\rZ\t팸\n \n0N\t\n\t \n\n쾺 "}}
   {:kind :wait}
   {:kind :read,
    :url
    "http://localhost:3011/eKtOX6K7001010859.%EC%80%8F+%EB%B1%A6-M%EB%A7%B6%EB%92%83%EA%BC%BD_%EB%9B%8E_%EB%82%AA%EB%83%97F%21%2C%EB%AB%83-%ED%93%80",
    :id "eKtOX6K7001010859",
    :post
    {:kur.blog.post/id "eKtOX6K7001010859",
     :kur.blog.post/meta-str "-",
     :kur.blog.post/title "쀏+뱦-M맶뒃꼽_뛎_낪냗F!,뫃-퓀",
     :kur.blog.post/public? nil,
     :kur.blog.post/path "test/fixture/post-md/eKtOX6K7001010859.-.쀏+뱦-M맶뒃꼽_뛎_낪냗F!,뫃-퓀.md",
     :md-text "걿\n끆뻿\r큈6D\n\r뉂\n횭\t\r\n\nz\ts풟 \r"}}]],
```

</details>

```clojure
 :result #error {
 :cause "Invalid V8 thread access: current thread is Thread[nREPL-session-92511f8f-1898-4fcf-aeea-caab236e304b,5,main] while the locker has thread Thread[nREPL-session-92511f8f-1898-4fcf-aeea-caab236e304b,5,]"
 :via
 [{:type java.lang.Error
   :message "Invalid V8 thread access: current thread is Thread[nREPL-session-92511f8f-1898-4fcf-aeea-caab236e304b,5,main] while the locker has thread Thread[nREPL-session-92511f8f-1898-4fcf-aeea-caab236e304b,5,]"
   :at [com.eclipsesource.v8.V8Locker checkThread "V8Locker.java" 99]}]
 :trace
 [[com.eclipsesource.v8.V8Locker checkThread "V8Locker.java" 99]
  [com.eclipsesource.v8.V8 checkThread "V8.java" 771]
  [com.eclipsesource.v8.V8Object <init> "V8Object.java" 43]
  [com.eclipsesource.v8.V8Object <init> "V8Object.java" 37]
  [com.eclipsesource.v8.V8Array <init> "V8Array.java" 32]
  [com.eclipsesource.v8.V8Object executeJSFunction "V8Object.java" 394]
  [jdk.internal.reflect.GeneratedMethodAccessor19 invoke nil -1]
  [jdk.internal.reflect.DelegatingMethodAccessorImpl invoke "DelegatingMethodAccessorImpl.java" 43]
  [java.lang.reflect.Method invoke "Method.java" 566]
  [clojure.lang.Reflector invokeMatchingMethod "Reflector.java" 167]
  [clojure.lang.Reflector invokeInstanceMethod "Reflector.java" 102]
  [kur.blog.md2x$obsidian_html invokeStatic "md2x.clj" 12]
  [kur.blog.md2x$obsidian_html invoke "md2x.clj" 11]
  [kur.blog.page.post$post_html invokeStatic "post.clj" 11]
  [kur.blog.page.post$post_html invoke "post.clj" 9]
  [kur.blog_test.spbt$run_model invokeStatic "spbt.clj" 106]
  [kur.blog_test.spbt$run_model invoke "spbt.clj" 97]
  [kur.blog_test.spbt$model_test$fn__23619$fn__23620 invoke "NO_SOURCE_FILE" 166]
  [kur.blog_test.spbt$model_test$fn__23619 invoke "NO_SOURCE_FILE" 164]
  [clojure.lang.AFn applyToHelper "AFn.java" 154]
  [clojure.lang.AFn applyTo "AFn.java" 144]
  [clojure.core$apply invokeStatic "core.clj" 667]
  [clojure.core$apply invoke "core.clj" 662]
  [clojure.test.check.properties$apply_gen$fn__6805$fn__6806 invoke "properties.cljc" 31]
  [clojure.test.check.properties$apply_gen$fn__6805 invoke "properties.cljc" 30]
  [clojure.test.check.rose_tree$fmap invokeStatic "rose_tree.cljc" 77]
  [clojure.test.check.rose_tree$fmap invoke "rose_tree.cljc" 73]
  [clojure.test.check.generators$fmap$fn__6152 invoke "generators.cljc" 104]
  [clojure.test.check.generators$gen_fmap$fn__6126 invoke "generators.cljc" 59]
  [clojure.test.check.generators$call_gen invokeStatic "generators.cljc" 43]
  [clojure.test.check.generators$call_gen invoke "generators.cljc" 39]
  [clojure.test.check$quick_check invokeStatic "check.cljc" 211]
  [clojure.test.check$quick_check doInvoke "check.cljc" 59]
  [clojure.lang.RestFn applyTo "RestFn.java" 142]
  [clojure.core$apply invokeStatic "core.clj" 671]
  [clojure.core$apply invoke "core.clj" 662]
  [kur.blog_test.spbt$model_test invokeStatic "NO_SOURCE_FILE" 137]
  [kur.blog_test.spbt$model_test doInvoke "NO_SOURCE_FILE" 137]
  [clojure.lang.RestFn applyTo "RestFn.java" 139]
  [clojure.core$apply invokeStatic "core.clj" 669]
  [clojure.core$apply invoke "core.clj" 662]
  [kur.blog_test.spbt$model_test invokeStatic "NO_SOURCE_FILE" 137]
  [kur.blog_test.spbt$model_test invoke "NO_SOURCE_FILE" 137]
  [kur.blog_test.spbt$eval23633 invokeStatic "NO_SOURCE_FILE" 230]
  [kur.blog_test.spbt$eval23633 invoke "NO_SOURCE_FILE" 230]
  [clojure.lang.Compiler eval "Compiler.java" 7194]
  [clojure.lang.Compiler eval "Compiler.java" 7184]
  [clojure.lang.Compiler eval "Compiler.java" 7149]
  [clojure.core$eval invokeStatic "core.clj" 3215]
  [clojure.core$eval invoke "core.clj" 3211]
  [nrepl.middleware.interruptible_eval$evaluate$fn__1245$fn__1246 invoke "interruptible_eval.clj" 87]
  [clojure.lang.AFn applyToHelper "AFn.java" 152]
  [clojure.lang.AFn applyTo "AFn.java" 144]
  [clojure.core$apply invokeStatic "core.clj" 667]
  [clojure.core$with_bindings_STAR_ invokeStatic "core.clj" 1990]
  [clojure.core$with_bindings_STAR_ doInvoke "core.clj" 1990]
  [clojure.lang.RestFn invoke "RestFn.java" 425]
  [nrepl.middleware.interruptible_eval$evaluate$fn__1245 invoke "interruptible_eval.clj" 87]
  [clojure.main$repl$read_eval_print__9206$fn__9209 invoke "main.clj" 437]
  [clojure.main$repl$read_eval_print__9206 invoke "main.clj" 437]
  [clojure.main$repl$fn__9215 invoke "main.clj" 458]
  [clojure.main$repl invokeStatic "main.clj" 458]
  [clojure.main$repl doInvoke "main.clj" 368]
  [clojure.lang.RestFn invoke "RestFn.java" 1523]
  [nrepl.middleware.interruptible_eval$evaluate invokeStatic "interruptible_eval.clj" 84]
  [nrepl.middleware.interruptible_eval$evaluate invoke "interruptible_eval.clj" 56]
  [nrepl.middleware.interruptible_eval$interruptible_eval$fn__1278$fn__1282 invoke "interruptible_eval.clj" 152]
  [clojure.lang.AFn run "AFn.java" 22]
  [nrepl.middleware.session$session_exec$main_loop__1348$fn__1352 invoke "session.clj" 218]
  [nrepl.middleware.session$session_exec$main_loop__1348 invoke "session.clj" 217]
  [clojure.lang.AFn run "AFn.java" 22]
  [java.lang.Thread run "Thread.java" 829]]},
 :result-data #:clojure.test.check.properties{:error #error {
 :cause "Invalid V8 thread access: current thread is Thread[nREPL-session-92511f8f-1898-4fcf-aeea-caab236e304b,5,main] while the locker has thread Thread[nREPL-session-92511f8f-1898-4fcf-aeea-caab236e304b,5,]"
 :via
 [{:type java.lang.Error
   :message "Invalid V8 thread access: current thread is Thread[nREPL-session-92511f8f-1898-4fcf-aeea-caab236e304b,5,main] while the locker has thread Thread[nREPL-session-92511f8f-1898-4fcf-aeea-caab236e304b,5,]"
   :at [com.eclipsesource.v8.V8Locker checkThread "V8Locker.java" 99]}]
 :trace
 [[com.eclipsesource.v8.V8Locker checkThread "V8Locker.java" 99]
  [com.eclipsesource.v8.V8 checkThread "V8.java" 771]
  [com.eclipsesource.v8.V8Object <init> "V8Object.java" 43]
  [com.eclipsesource.v8.V8Object <init> "V8Object.java" 37]
  [com.eclipsesource.v8.V8Array <init> "V8Array.java" 32]
  [com.eclipsesource.v8.V8Object executeJSFunction "V8Object.java" 394]
  [jdk.internal.reflect.GeneratedMethodAccessor19 invoke nil -1]
  [jdk.internal.reflect.DelegatingMethodAccessorImpl invoke "DelegatingMethodAccessorImpl.java" 43]
  [java.lang.reflect.Method invoke "Method.java" 566]
  [clojure.lang.Reflector invokeMatchingMethod "Reflector.java" 167]
  [clojure.lang.Reflector invokeInstanceMethod "Reflector.java" 102]
  [kur.blog.md2x$obsidian_html invokeStatic "md2x.clj" 12]
  [kur.blog.md2x$obsidian_html invoke "md2x.clj" 11]
  [kur.blog.page.post$post_html invokeStatic "post.clj" 11]
  [kur.blog.page.post$post_html invoke "post.clj" 9]
  [kur.blog_test.spbt$run_model invokeStatic "spbt.clj" 106]
  [kur.blog_test.spbt$run_model invoke "spbt.clj" 97]
  [kur.blog_test.spbt$model_test$fn__23619$fn__23620 invoke "NO_SOURCE_FILE" 166]
  [kur.blog_test.spbt$model_test$fn__23619 invoke "NO_SOURCE_FILE" 164]
  [clojure.lang.AFn applyToHelper "AFn.java" 154]
  [clojure.lang.AFn applyTo "AFn.java" 144]
  [clojure.core$apply invokeStatic "core.clj" 667]
  [clojure.core$apply invoke "core.clj" 662]
  [clojure.test.check.properties$apply_gen$fn__6805$fn__6806 invoke "properties.cljc" 31]
  [clojure.test.check.properties$apply_gen$fn__6805 invoke "properties.cljc" 30]
  [clojure.test.check.rose_tree$fmap invokeStatic "rose_tree.cljc" 77]
  [clojure.test.check.rose_tree$fmap invoke "rose_tree.cljc" 73]
  [clojure.test.check.generators$fmap$fn__6152 invoke "generators.cljc" 104]
  [clojure.test.check.generators$gen_fmap$fn__6126 invoke "generators.cljc" 59]
  [clojure.test.check.generators$call_gen invokeStatic "generators.cljc" 43]
  [clojure.test.check.generators$call_gen invoke "generators.cljc" 39]
  [clojure.test.check$quick_check invokeStatic "check.cljc" 211]
  [clojure.test.check$quick_check doInvoke "check.cljc" 59]
  [clojure.lang.RestFn applyTo "RestFn.java" 142]
  [clojure.core$apply invokeStatic "core.clj" 671]
  [clojure.core$apply invoke "core.clj" 662]
  [kur.blog_test.spbt$model_test invokeStatic "NO_SOURCE_FILE" 137]
  [kur.blog_test.spbt$model_test doInvoke "NO_SOURCE_FILE" 137]
  [clojure.lang.RestFn applyTo "RestFn.java" 139]
  [clojure.core$apply invokeStatic "core.clj" 669]
  [clojure.core$apply invoke "core.clj" 662]
  [kur.blog_test.spbt$model_test invokeStatic "NO_SOURCE_FILE" 137]
  [kur.blog_test.spbt$model_test invoke "NO_SOURCE_FILE" 137]
  [kur.blog_test.spbt$eval23633 invokeStatic "NO_SOURCE_FILE" 230]
  [kur.blog_test.spbt$eval23633 invoke "NO_SOURCE_FILE" 230]
  [clojure.lang.Compiler eval "Compiler.java" 7194]
  [clojure.lang.Compiler eval "Compiler.java" 7184]
  [clojure.lang.Compiler eval "Compiler.java" 7149]
  [clojure.core$eval invokeStatic "core.clj" 3215]
  [clojure.core$eval invoke "core.clj" 3211]
  [nrepl.middleware.interruptible_eval$evaluate$fn__1245$fn__1246 invoke "interruptible_eval.clj" 87]
  [clojure.lang.AFn applyToHelper "AFn.java" 152]
  [clojure.lang.AFn applyTo "AFn.java" 144]
  [clojure.core$apply invokeStatic "core.clj" 667]
  [clojure.core$with_bindings_STAR_ invokeStatic "core.clj" 1990]
  [clojure.core$with_bindings_STAR_ doInvoke "core.clj" 1990]
  [clojure.lang.RestFn invoke "RestFn.java" 425]
  [nrepl.middleware.interruptible_eval$evaluate$fn__1245 invoke "interruptible_eval.clj" 87]
  [clojure.main$repl$read_eval_print__9206$fn__9209 invoke "main.clj" 437]
  [clojure.main$repl$read_eval_print__9206 invoke "main.clj" 437]
  [clojure.main$repl$fn__9215 invoke "main.clj" 458]
  [clojure.main$repl invokeStatic "main.clj" 458]
  [clojure.main$repl doInvoke "main.clj" 368]
  [clojure.lang.RestFn invoke "RestFn.java" 1523]
  [nrepl.middleware.interruptible_eval$evaluate invokeStatic "interruptible_eval.clj" 84]
  [nrepl.middleware.interruptible_eval$evaluate invoke "interruptible_eval.clj" 56]
  [nrepl.middleware.interruptible_eval$interruptible_eval$fn__1278$fn__1282 invoke "interruptible_eval.clj" 152]
  [clojure.lang.AFn run "AFn.java" 22]
  [nrepl.middleware.session$session_exec$main_loop__1348$fn__1352 invoke "session.clj" 218]
  [nrepl.middleware.session$session_exec$main_loop__1348 invoke "session.clj" 217]
  [clojure.lang.AFn run "AFn.java" 22]
  [java.lang.Thread run "Thread.java" 829]]}},
 :failing-size 14,
 :pass? false}
```