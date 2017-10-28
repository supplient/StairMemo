#类文档
* 创建于2017.10.28
* 最后修改于2017.10.28

##MainActivity
MainActivity使用DrawerLayout实现侧栏效果，并包含三个组件：

* ToolBar

    当模式切换时，ToolBar对应切换。

* FragmentContainer

    当模式切换时，FragmentContainer所包含的Fragment对应切换。

* NavigationView

    NavigationView实现侧栏视图，起到切换不同模式的功能。

###重载函数
(注：非平凡重载，以下均如此。）

* void onBackPressed()

    当后退键被点击时，按照如下逻辑：
    
    1. 若侧栏被打开，则关闭侧栏、结束逻辑。
    2. 若当前Fragment响应onBackPressed调用，则结束逻辑。
    3. 调用父类onBackPressed。

###私有函数
(注；仅关乎逻辑核心的私有函数，以下均如此。)

* void SwitchToFragment(Fragment fragment)

    当侧栏中导航按钮被点击，则切换至对应Fragment。切换过程按照如下逻辑：
    
    1. 更换FragmentContainer的包含Fragment.
    2. 调用对应Fragment的onCreateOptionsMenu来切换ToolBar.

##MemoFragment
MemoFragment包含三个组件：

1. MemoList

    单词本显示的主体，其中每一项都由GetWordLayout()给出。  
    以ScrollView为容器视图实现可滚动的效果。

2. AddFloatingActionButton

    用来添加新单词。

3. MemoMenu

    提供ToolBar所需的Memo模式对应的Item.  

###常量
####Request Code
| Types         | Name          | Value |
| ------------- |:-------------:| -----:|
| int      		| ADD_WORD		| 128	|
| int      		| EDIT_WORD     | 129	|

####Result Code
| Types         | Name          | Value |
| ------------- |:-------------:| -----:|
| int			| RESULT_OK		| 1		|
| int			| RESULT_CANCEL	| 2		|

####Data Name
| Types         | Name          | Value |
| ------------- |:-------------:| -----:|
| String		| DATA_WORD_INDEX| com.supplient.stairmemo.data_word_index|
| String		| DATA_MEANINGS |com.supplient.stairmemo.data_meanings|
| String		| DATA_PRIORITY |com.supplient.stairmemo.data_priority|

###重载函数

* boolean onBackPressed()

    若开启了多选模式，则关闭多选模式且响应(返回true)，否则不响应。

###私有函数
####View Plugins
* LinearLayout GetWordLayout(Word word, int index)

    根据所给参数构造MemoList中的一项的视图。

####Data Plugins
* void AddWord(ArrayList<String> meanings, int priority, boolean update)

    向MemoBook中添加由所给参数构造出来的Word，并根据update判断是否更新MemoList.

####View-Data Plugins
* void InitList(LinearLayout list)

    将MemoBook中的数据填充进list中。

* void UpdateList(LinearLayout list)

    根据MemoBook中的数据更新list。

###私有类
####Listener
* OnWordMeaningLayoutClickListener

    当单词被点击时响应，打开EditActivity.

* OnWordMeaningLayoutLongClickListener

    当单词被长按时响应，进入多选模式。


##ReciteFragment
ReciteFragment包含两个组件：

1. ReciteLayout

    ReciteFragment主体。

2. ReciteMenu

    提供ToolBar所需的Recite模式对应的Item. 

###私有函数
####Logic Parts
* boolean IterateWord()

    返回true，当当前Word已经读完；否则返回false.  
	迭代nowWord的meaning，或若已经读完nowWord的meaning，则至下一个Word。

####View Plugins
* void addNowMeaningView()

    向ReciteLayout添加由当前Word的下一个meaning填充的由GetMeaningView得到的视图。

* View GetMeaningView(String s)

    根据参数填充构造MeaningView.


###私有类
####Listener
* OnLayoutClickListener

	当ReciteLayout被点击时响应，调用IterateWord进行meaning及Word的迭代。

##AddActivity
AddActivity为子Activity(指只可由其它Activity调用的Activity），其由三个部件组成：

1. AddLinearLayout

	以列表显示各个meaning，其中各项由GetOneRow给出。

2. SaveFloatingActionButton

	结束AddActivity，将结果以Intent形式返回。

3. AddMenu

    提供ToolBar所需的AddActivity对应的Item.

###重载函数
* void onBackPressed()

	结束AddActivity，返回CANCEL的Intent
###保护函数
####View Plugins
* LinearLayout GetOneRow()
* LinearLayout GetOneRow(String text)

	构造一个空的meaningView，或者将text填充进去。

####Intent Plugins
* Intent GetResultIntent()

	根据视图中的信息构造Intent。

###保护类
####Listener
* OnSaveFabClickListener
* OnNewMeaningButtonClickListener

	调用GetOneRow添加新的meaningView。

* OnDelButtonClicked

	删除对应的meaningView。

##EditActivity
派生自AddActivity.

###重载函数
* void onCreate(Bundle savedInstanceState)

	调用AddActivity的onCreate创建视图后，提取Intent的信息，将其填充至视图。

* Intent GetResultIntent()

	在AddActivity返回的Intent的基础上增加editingIndex数据。

##MemoBook
单例类  
派生自ArrayList\<Word\>.  
管理所有Word数据，控制与Word相关的文件操作。

###常量
####File Constants
| Types         | Name          | Value |
| ------------- |:-------------:| -----:|
| String		| fileName		| memobook.txt|

###公有函数
####Interface
* Word GetLowestOrderWord()

	返回背诵顺序最靠前的Word，并增加该Word的已背诵次数。

##Word
单词的数据类。

###属性
| Name | Read | Write |
| :---:  | :--: | :----: |
| timeStamp| x | x |
| priority | o | o |
| reciteTime | x | o |
| meanings | o | x |
| order | o | x |

###公有函数
####Constructors
* Word()
* Word(ArrayList<String> meanings, int priority)
* Word(String str)

	注：str必须与Word.ToString导出的字符串格式一致。

####Type Plugins
* String ToString()
* void FromString(String str)

	注：str必须与ToString导出的字符串格式一致。

##DefaultOptions
包括各类配置数值。

###常量
| Type | Name | Value |
| :--:| :--:| :--: |
| int | defaultPriority | 3|
| int | maxPriority | 5 |
| int | minPriority | 1 |
| int | maxMeanings | 10 |
